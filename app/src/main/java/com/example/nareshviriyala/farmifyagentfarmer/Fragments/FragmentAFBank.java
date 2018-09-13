package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONObject;

public class FragmentAFBank extends Fragment implements View.OnClickListener {
    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private JSONObject farmerbankData;
    private Validations validations;
    private EditText input_accountname, input_accountnumber, input_confirmaccountnumber, input_bankname, input_branchname, input_ifsc, input_district, input_state;
    private Button btn_bankdatasave;

    private TextInputLayout input_layout_confirmaccountnumber;

    public FragmentAFBank(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_af_bank, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            validations = new Validations();
            String data = dbHelper.getParameter(getString(R.string.Bank));
            //dbHelper.deleteParameter(getString(R.string.Bank));
            //data = dbHelper.getParameter(getString(R.string.Bank));
            if(data.isEmpty() || data == null)
                farmerbankData = new JSONObject();
            else
                farmerbankData = new JSONObject(data);
            ((HomeActivity) getActivity()).setActionBarTitle("Bank Details");

            input_accountname = rootView.findViewById(R.id.input_accountname);
            input_accountnumber = rootView.findViewById(R.id.input_accountnumber);
            input_confirmaccountnumber = rootView.findViewById(R.id.input_confirmaccountnumber);
            input_bankname = rootView.findViewById(R.id.input_bankname);
            input_branchname = rootView.findViewById(R.id.input_branchname);
            input_ifsc = rootView.findViewById(R.id.input_ifsc);
            input_district = rootView.findViewById(R.id.input_district);
            input_state = rootView.findViewById(R.id.input_state);
            btn_bankdatasave = rootView.findViewById(R.id.btn_bankdatasave);
            input_layout_confirmaccountnumber = rootView.findViewById(R.id.input_layout_confirmaccountnumber);

            input_accountname.addTextChangedListener(new MyTextWatcher(input_accountname));
            input_accountnumber.addTextChangedListener(new MyTextWatcher(input_accountnumber));
            input_confirmaccountnumber.addTextChangedListener(new MyTextWatcher(input_confirmaccountnumber));
            input_bankname.addTextChangedListener(new MyTextWatcher(input_bankname));
            input_branchname.addTextChangedListener(new MyTextWatcher(input_branchname));
            input_ifsc.addTextChangedListener(new MyTextWatcher(input_ifsc));
            input_district.addTextChangedListener(new MyTextWatcher(input_district));
            input_state.addTextChangedListener(new MyTextWatcher(input_state));

            rootView.findViewById(R.id.rb_savings).setOnClickListener(this);
            rootView.findViewById(R.id.rb_current).setOnClickListener(this);
            rootView.findViewById(R.id.rb_other).setOnClickListener(this);
            btn_bankdatasave.setOnClickListener(this);
            populateForm();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
        return rootView;
    }

    private void populateForm(){
        try{
            if(farmerbankData.has("AccountName"))
                input_accountname.setText(farmerbankData.getString("AccountName"));
            if(farmerbankData.has("AccountNumber"))
                input_accountnumber.setText(farmerbankData.getString("AccountNumber"));
            if(farmerbankData.has("ConfirmAccountNumber"))
                input_confirmaccountnumber.setText(farmerbankData.getString("ConfirmAccountNumber"));
            if(farmerbankData.has("BankName"))
                input_bankname.setText(farmerbankData.getString("BankName"));
            if(farmerbankData.has("BranchName"))
                input_branchname.setText(farmerbankData.getString("BranchName"));
            if(farmerbankData.has("IFSC"))
                input_ifsc.setText(farmerbankData.getString("IFSC"));
            if(farmerbankData.has("District"))
                input_district.setText(farmerbankData.getString("District"));
            if(farmerbankData.has("State"))
                input_state.setText(farmerbankData.getString("State"));
            if(farmerbankData.has("District"))
                input_district.setText(farmerbankData.getString("District"));
            if(farmerbankData.has("State"))
                input_state.setText(farmerbankData.getString("State"));


            if(farmerbankData.has("AccountType")){
                RadioGroup rg_cast = rootView.findViewById(R.id.rg_actype);
                switch (farmerbankData.getString("AccountType")){
                    case "Savings":
                        rg_cast.check(R.id.rb_savings);
                        break;
                    case "Current":
                        rg_cast.check(R.id.rb_current);
                        break;
                    case "Other":
                        rg_cast.check(R.id.rb_other);
                        break;
                }
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

        public void afterTextChanged(Editable editable){
            try {
                switch (view.getId()) {
                    case R.id.input_accountname:
                        farmerbankData.put("AccountName", input_accountname.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_accountnumber:
                        farmerbankData.put("AccountNumber", input_accountnumber.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_confirmaccountnumber:
                        farmerbankData.put("ConfirmAccountNumber", input_confirmaccountnumber.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_bankname:
                        farmerbankData.put("BankName", input_bankname.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_branchname:
                        farmerbankData.put("BranchName", input_branchname.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_ifsc:
                        farmerbankData.put("IFSC", input_ifsc.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_district:
                        farmerbankData.put("District", input_district.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                    case R.id.input_state:
                        farmerbankData.put("State", input_state.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                        break;
                }
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        try {
            boolean checked = false;
            if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();

            switch (v.getId()) {
                case R.id.btn_bankdatasave:
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    validateBankData();
                    break;
                //cast radio button group
                case R.id.rb_savings:
                    if (checked) {
                        farmerbankData.put("AccountType", "Savings");
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                    }
                    break;
                case R.id.rb_current:
                    if (checked) {
                        farmerbankData.put("AccountType", "Current");
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                    }
                    break;
                case R.id.rb_other:
                    if (checked) {
                        farmerbankData.put("AccountType", "Other");
                        dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
                    }
                    break;
                default:
                    break;
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validateBankData(){
        try{
            dbHelper.setParameter(getString(R.string.Bank), farmerbankData.toString());
            dbHelper.setParameter(getString(R.string.BankStatus), "3");
            if (!validateConfirmAccountNumber())
                return;

            if(!farmerbankData.has("AccountName") || farmerbankData.getString("AccountName").equalsIgnoreCase("")
                    || !farmerbankData.has("AccountNumber") || farmerbankData.getString("AccountNumber").equalsIgnoreCase("")
                    || !farmerbankData.has("BankName") || farmerbankData.getString("BankName").equalsIgnoreCase("")
                    || !farmerbankData.has("IFSC") || farmerbankData.getString("IFSC").equalsIgnoreCase("")
                    || !farmerbankData.has("AccountType") || farmerbankData.getString("AccountType").equalsIgnoreCase("")){
                dbHelper.setParameter(getString(R.string.BankStatus), "1");
            }else if(!farmerbankData.has("BranchName") || farmerbankData.getString("BranchName").equalsIgnoreCase("")
                    || !farmerbankData.has("District") || farmerbankData.getString("District").equalsIgnoreCase("")
                    || !farmerbankData.has("State") || farmerbankData.getString("State").equalsIgnoreCase("")){
                dbHelper.setParameter(getString(R.string.BankStatus), "2");
            }
            goBack();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private boolean validateConfirmAccountNumber() {
        String val = validations.validateConfirmPassword(input_accountnumber.getText().toString().trim(), input_confirmaccountnumber.getText().toString().trim());
        if (val != null) {
            input_layout_confirmaccountnumber.setError(val);
            requestFocus(input_confirmaccountnumber);
            return false;
        } else {
            input_layout_confirmaccountnumber.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void goBack(){
        try{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {
                getFragmentManager().popBackStack();
                return;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

}
