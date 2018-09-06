package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddCreditItem;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentCommerce extends Fragment implements View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private JSONObject farmercommerceData;
    private Validations validations;
    private Button btn_commercedatasave;
    private EditText input_annualincome, input_incomefromcrops, input_feother;
    private FloatingActionButton fab_addcredit;
    private AdapterCreditInformation adapterCreditInformation;
    private ListView lv_creditlist;

    public FragmentCommerce(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_commerce, container, false);

        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            validations = new Validations();
            //dbHelper.deleteParameter(getString(R.string.Commerce));
            String data = dbHelper.getParameter(getString(R.string.Commerce));
            if(data.isEmpty() || data == null)
                farmercommerceData = new JSONObject();
            else
                farmercommerceData = new JSONObject(data);
            ((HomeActivity) getActivity()).setActionBarTitle("Commerce Details");
            btn_commercedatasave = rootView.findViewById(R.id.btn_commercedatasave);
            btn_commercedatasave.setOnClickListener(this);

            input_annualincome = rootView.findViewById(R.id.input_annualincome);
            input_incomefromcrops = rootView.findViewById(R.id.input_incomefromcrops);
            input_feother = rootView.findViewById(R.id.input_feother);

            lv_creditlist = rootView.findViewById(R.id.lv_creditlist);

            input_annualincome.addTextChangedListener(new MyTextWatcher(input_annualincome));
            input_incomefromcrops.addTextChangedListener(new MyTextWatcher(input_incomefromcrops));
            input_feother.addTextChangedListener(new MyTextWatcher(input_feother));

            rootView.findViewById(R.id.chk_febankloan).setOnClickListener(this);
            rootView.findViewById(R.id.chk_femoneylender).setOnClickListener(this);
            rootView.findViewById(R.id.chk_fecother).setOnClickListener(this);

            rootView.findViewById(R.id.rb_credittakenyes).setOnClickListener(this);
            rootView.findViewById(R.id.rb_credittakenno).setOnClickListener(this);

            fab_addcredit = rootView.findViewById(R.id.fab_addcredit);
            fab_addcredit.setOnClickListener(this);

            populateForm();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void populateForm() {
        try {
            if(farmercommerceData.has("AnnualIncome"))
                input_annualincome.setText(farmercommerceData.getString("AnnualIncome"));
            if(farmercommerceData.has("CropIncome"))
                input_incomefromcrops.setText(farmercommerceData.getString("CropIncome"));

            if(farmercommerceData.has("FarmExpenseSource")){
                JSONArray expns = farmercommerceData.getJSONArray("FarmExpenseSource");
                for(int i = 0; i < expns.length(); i++){
                    String exp = expns.get(i).toString();
                    switch (exp){
                        case "Bank":
                            ((CheckBox)rootView.findViewById(R.id.chk_febankloan)).setChecked(true);
                            break;
                        case "MoneyLender":
                            ((CheckBox)rootView.findViewById(R.id.chk_femoneylender)).setChecked(true);
                            break;
                        case "Other":
                            ((CheckBox)rootView.findViewById(R.id.chk_fecother)).setChecked(true);
                            break;
                    }
                }
            }
            if(farmercommerceData.has("FarmExpenseSourceOther")) {
                input_feother.setText(farmercommerceData.getString("FarmExpenseSourceOther"));
                input_feother.setVisibility(View.VISIBLE);
            }

            refreshCreditListView();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshCreditListView(){
        try{
            ArrayList<ModelCreditInformation> creditInformation = new ArrayList<>();
            farmercommerceData = new JSONObject(dbHelper.getParameter(getString(R.string.Commerce)));
            JSONArray creditList = farmercommerceData.getJSONArray("CreditInformation");
            for(int i = 0; i < creditList.length(); i++){
                JSONObject item = creditList.getJSONObject(i);
                creditInformation.add(new ModelCreditInformation(item.getInt("Id")
                        , item.getString("Source")
                        , item.getString("Date")
                        , item.getString("Amount")
                        , item.getString("Interest")
                        , item.getBoolean("Paid")
                        , item.getString("PendingAmount")));
            }
            if(creditInformation.size() > 0)
                rootView.findViewById(R.id.ll_creditlist).setVisibility(View.VISIBLE);
            else
                rootView.findViewById(R.id.ll_creditlist).setVisibility(View.GONE);
            if(adapterCreditInformation == null) {
                adapterCreditInformation = new AdapterCreditInformation(this.getContext(), creditInformation, this);
                lv_creditlist.setAdapter(adapterCreditInformation);
            }else
                adapterCreditInformation.updateCreditList(creditInformation);

            if(getView() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            boolean checked = false;
            if(v instanceof CheckBox)
                checked = ((CheckBox) v).isChecked();
            else if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();

            switch (v.getId()) {
                case R.id.chk_febankloan:
                    modifyFarmExpenseArray();
                    break;
                case R.id.chk_femoneylender:
                    modifyFarmExpenseArray();
                    break;
                case R.id.chk_fecother:
                    if (checked)
                        rootView.findViewById(R.id.input_layout_feother).setVisibility(View.VISIBLE);
                    else
                        rootView.findViewById(R.id.input_layout_feother).setVisibility(View.GONE);
                    modifyFarmExpenseArray();
                    break;
                case R.id.rb_credittakenyes:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabcontainer).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_credittakenno:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabcontainer).setVisibility(View.GONE);
                    break;
                case R.id.fab_addcredit:
                    new DialogAddCreditItem(getActivity(), null, this).show();
                    break;
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

    }

    private void modifyFarmExpenseArray(){
        try{
            farmercommerceData.remove("FarmExpenseSource");
            JSONArray srcInfo = new JSONArray();

            if(((CheckBox)rootView.findViewById(R.id.chk_febankloan)).isChecked())
                srcInfo.put("Bank");
            if(((CheckBox)rootView.findViewById(R.id.chk_femoneylender)).isChecked())
                srcInfo.put("MoneyLender");
            if(((CheckBox)rootView.findViewById(R.id.chk_fecother)).isChecked()) {
                srcInfo.put("Other");
                farmercommerceData.put("FarmExpenseSourceOther",farmercommerceData.has("FarmExpenseSourceOther")?farmercommerceData.getString("FarmExpenseSourceOther"):"");
            }
            else
                farmercommerceData.remove("FarmExpenseSourceOther");

            if(srcInfo.length() > 0)
                farmercommerceData.put("FarmExpenseSource", srcInfo);
            dbHelper.setParameter(getString(R.string.Commerce), farmercommerceData.toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
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
                    case R.id.input_annualincome:
                        farmercommerceData.put("AnnualIncome", input_annualincome.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Commerce), farmercommerceData.toString());
                        break;
                    case R.id.input_incomefromcrops:
                        farmercommerceData.put("CropIncome", input_incomefromcrops.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Commerce), farmercommerceData.toString());
                        break;
                    case R.id.input_feother:
                        farmercommerceData.put("FarmExpenseSourceOther", input_feother.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Commerce), farmercommerceData.toString());
                        break;
                }
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
            }
        }
    }

}