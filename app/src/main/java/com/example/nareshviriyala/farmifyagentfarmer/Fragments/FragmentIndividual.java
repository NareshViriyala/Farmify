package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class FragmentIndividual extends Fragment implements View.OnClickListener{
    private View rootView;
    private LogErrors logErrors;
    private String className;
    private EditText input_phone, input_aadhar, input_otp, input_firstname, input_lastname, input_surname, input_age, input_address1, input_address2, input_state, input_district, input_villagetown, input_pincode;
    private TextInputLayout input_layout_phone, input_layout_aadhar, input_layout_otp, input_layout_firstname, input_layout_lastname, input_layout_surname, input_layout_age;
    private Button btn_verifyAadharandphone, btn_sendotp, btn_individualdatasave;
    private LinearLayout ll_verifyotp, ll_showfarmerphoneandaadhar, ll_verifyfarmerphoneandaadhar, ll_individualform;
    private ImageView img_calendar;
    private int day, month, year;
    public Validations validations;
    private ProgressBar pb_loadingaadharverify, pb_loadingotp;
    private JSONObject farmerIdvData;
    private TextView tv_aadhar, tv_phone;
    private DatabaseHelper dbHelper;

    public FragmentIndividual(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_individual, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            String data = dbHelper.getParameter(getString(R.string.Individual));
            if(data.isEmpty() || data == null)
                farmerIdvData = new JSONObject();
            else
                farmerIdvData = new JSONObject(data);
            validations = new Validations();
            ((HomeActivity) getActivity()).setActionBarTitle("Farmer's Details");

            btn_verifyAadharandphone = rootView.findViewById(R.id.btn_verifyAadharandphone);
            btn_sendotp = rootView.findViewById(R.id.btn_sendotp);
            btn_individualdatasave = rootView.findViewById(R.id.btn_individualdatasave);
            img_calendar = rootView.findViewById(R.id.img_calendar);

            btn_verifyAadharandphone.setOnClickListener(this);
            btn_sendotp.setOnClickListener(this);
            img_calendar.setOnClickListener(this);
            btn_individualdatasave.setOnClickListener(this);

            tv_aadhar = rootView.findViewById(R.id.tv_aadhar);
            tv_phone = rootView.findViewById(R.id.tv_phone);

            input_layout_aadhar = rootView.findViewById(R.id.input_layout_aadhar);
            input_layout_phone = rootView.findViewById(R.id.input_layout_phone);
            input_layout_otp = rootView.findViewById(R.id.input_layout_otp);
            input_layout_firstname = rootView.findViewById(R.id.input_layout_firstname);
            input_layout_lastname = rootView.findViewById(R.id.input_layout_lastname);
            input_layout_surname = rootView.findViewById(R.id.input_layout_surname);
            input_layout_age = rootView.findViewById(R.id.input_layout_age);

            input_aadhar = rootView.findViewById(R.id.input_aadhar);
            input_phone = rootView.findViewById(R.id.input_phone);
            input_otp = rootView.findViewById(R.id.input_otp);
            input_firstname = rootView.findViewById(R.id.input_firstname);
            input_lastname = rootView.findViewById(R.id.input_lastname);
            input_surname = rootView.findViewById(R.id.input_surname);
            input_age = rootView.findViewById(R.id.input_age);
            input_address1 = rootView.findViewById(R.id.input_address1);
            input_address2 = rootView.findViewById(R.id.input_address2);
            input_state = rootView.findViewById(R.id.input_state);
            input_district = rootView.findViewById(R.id.input_district);
            input_villagetown = rootView.findViewById(R.id.input_villagetown);
            input_pincode = rootView.findViewById(R.id.input_pincode);

            ll_verifyotp = rootView.findViewById(R.id.ll_verifyotp);
            ll_showfarmerphoneandaadhar = rootView.findViewById(R.id.ll_showfarmerphoneandaadhar);
            ll_verifyfarmerphoneandaadhar = rootView.findViewById(R.id.ll_verifyfarmerphoneandaadhar);
            ll_individualform = rootView.findViewById(R.id.ll_individualform);

            pb_loadingaadharverify = rootView.findViewById(R.id.pb_loadingaadharverify);
            pb_loadingotp = rootView.findViewById(R.id.pb_loadingotp);

            input_aadhar.addTextChangedListener(new MyTextWatcher(input_aadhar));
            input_surname.addTextChangedListener(new MyTextWatcher(input_surname));
            input_firstname.addTextChangedListener(new MyTextWatcher(input_firstname));
            input_lastname.addTextChangedListener(new MyTextWatcher(input_lastname));
            input_address1.addTextChangedListener(new MyTextWatcher(input_address1));
            input_address2.addTextChangedListener(new MyTextWatcher(input_address2));
            input_state.addTextChangedListener(new MyTextWatcher(input_state));
            input_district.addTextChangedListener(new MyTextWatcher(input_district));
            input_villagetown.addTextChangedListener(new MyTextWatcher(input_villagetown));
            input_pincode.addTextChangedListener(new MyTextWatcher(input_pincode));

            rootView.findViewById(R.id.rb_cast_general).setOnClickListener(this);
            rootView.findViewById(R.id.rb_cast_st).setOnClickListener(this);
            rootView.findViewById(R.id.rb_cast_sc).setOnClickListener(this);
            rootView.findViewById(R.id.rb_cast_obc).setOnClickListener(this);
            rootView.findViewById(R.id.rb_cast_other).setOnClickListener(this);

            rootView.findViewById(R.id.rb_male).setOnClickListener(this);
            rootView.findViewById(R.id.rb_female).setOnClickListener(this);

            rootView.findViewById(R.id.rb_ftsmall).setOnClickListener(this);
            rootView.findViewById(R.id.rb_ftmarginal).setOnClickListener(this);
            rootView.findViewById(R.id.rb_ftother).setOnClickListener(this);

            rootView.findViewById(R.id.rb_fcowner).setOnClickListener(this);
            rootView.findViewById(R.id.rb_fctenant).setOnClickListener(this);
            rootView.findViewById(R.id.rb_fcsharecropper).setOnClickListener(this);

            populateForm();

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
        return rootView;
    }

    private void populateForm(){
        try{
            if(farmerIdvData.has("Aadhar") && farmerIdvData.has("Phone")) {
                ll_showfarmerphoneandaadhar.setVisibility(View.VISIBLE);
                ll_verifyfarmerphoneandaadhar.setVisibility(View.GONE);
                ll_individualform.setVisibility(View.VISIBLE);
                tv_aadhar.setText("Aadhar :"+farmerIdvData.getString("Aadhar"));
                tv_phone.setText("Phone :"+farmerIdvData.getString("Phone"));

                if(farmerIdvData.has("FirstName"))
                    input_firstname.setText(farmerIdvData.getString("FirstName"));
                if(farmerIdvData.has("LastName"))
                    input_lastname.setText(farmerIdvData.getString("LastName"));
                if(farmerIdvData.has("Surname"))
                    input_surname.setText(farmerIdvData.getString("Surname"));
                if(farmerIdvData.has("DOB"))
                    input_age.setText(farmerIdvData.getString("DOB"));
                if(farmerIdvData.has("Address1"))
                    input_address1.setText(farmerIdvData.getString("Address1"));
                if(farmerIdvData.has("Address2"))
                    input_address2.setText(farmerIdvData.getString("Address2"));
                if(farmerIdvData.has("State"))
                    input_state.setText(farmerIdvData.getString("State"));
                if(farmerIdvData.has("District"))
                    input_district.setText(farmerIdvData.getString("District"));
                if(farmerIdvData.has("VillageTown"))
                    input_villagetown.setText(farmerIdvData.getString("VillageTown"));
                if(farmerIdvData.has("Pincode"))
                    input_pincode.setText(farmerIdvData.getString("Pincode"));


                if(farmerIdvData.has("Cast")){
                    RadioGroup rg_cast = rootView.findViewById(R.id.rg_cast);
                    switch (farmerIdvData.getString("Cast")){
                        case "General":
                            rg_cast.check(R.id.rb_cast_general);
                            break;
                        case "SC":
                            rg_cast.check(R.id.rb_cast_sc);
                            break;
                        case "ST":
                            rg_cast.check(R.id.rb_cast_st);
                            break;
                        case "OBC":
                            rg_cast.check(R.id.rb_cast_obc);
                            break;
                        case "Other":
                            rg_cast.check(R.id.rb_cast_other);
                            break;
                    }
                }

                if(farmerIdvData.has("Gender")){
                    RadioGroup rg_gender = rootView.findViewById(R.id.rg_gender);

                    switch (farmerIdvData.getString("Gender")){
                        case "Male":
                            rg_gender.check(R.id.rb_male);
                            break;
                        case "Female":
                            rg_gender.check(R.id.rb_female);
                            break;
                    }
                }

                if(farmerIdvData.has("FarmerType")){
                    RadioGroup rg_farmertype = rootView.findViewById(R.id.rg_farmertype);
                    switch (farmerIdvData.getString("FarmerType")){
                        case "Small":
                            rg_farmertype.check(R.id.rb_ftsmall);
                            break;
                        case "Marginal":
                            rg_farmertype.check(R.id.rb_ftmarginal);
                            break;
                        case "Other":
                            rg_farmertype.check(R.id.rb_ftother);
                            break;
                    }
                }

                if(farmerIdvData.has("FarmType")){
                    RadioGroup rg_farmcategory = rootView.findViewById(R.id.rg_farmcategory);
                    switch (farmerIdvData.getString("FarmType")){
                        case "Owner":
                            rg_farmcategory.check(R.id.rb_fcowner);
                            break;
                        case "Tenant":
                            rg_farmcategory.check(R.id.rb_fctenant);
                            break;
                        case "ShareCropper":
                            rg_farmcategory.check(R.id.rb_fcsharecropper);
                            break;
                    }
                }
            }else{
                ll_individualform.setVisibility(View.GONE);
                ll_verifyotp.setVisibility(View.GONE);
                ll_showfarmerphoneandaadhar.setVisibility(View.GONE);
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
                    case R.id.input_aadhar:
                        if (input_aadhar.getText().toString().length() == 4 || input_aadhar.getText().toString().length() == 9) {
                            input_aadhar.setText(input_aadhar.getText().toString() + " ");
                            input_aadhar.setSelection(input_aadhar.getText().length());
                        } else if (input_aadhar.getText().toString().length() == 14)
                            requestFocus(input_phone);
                        break;
                    case R.id.input_firstname:
                        farmerIdvData.put("FirstName", input_firstname.getText().toString().trim());
                        break;
                    case R.id.input_lastname:
                        farmerIdvData.put("LastName", input_lastname.getText().toString().trim());
                        break;
                    case R.id.input_surname:
                        farmerIdvData.put("Surname", input_surname.getText().toString().trim());
                        break;
                    case R.id.input_address1:
                        farmerIdvData.put("Address1", input_address1.getText().toString().trim());
                        break;
                    case R.id.input_address2:
                        farmerIdvData.put("Address2", input_address2.getText().toString().trim());
                        break;
                    case R.id.input_state:
                        farmerIdvData.put("State", input_state.getText().toString().trim());
                        break;
                    case R.id.input_district:
                        farmerIdvData.put("District", input_district.getText().toString().trim());
                        break;
                    case R.id.input_villagetown:
                        farmerIdvData.put("VillageTown", input_villagetown.getText().toString().trim());
                        break;
                    case R.id.input_pincode:
                        farmerIdvData.put("Pincode", input_pincode.getText().toString().trim());
                        break;
                }
                dbHelper.setParameter(getString(R.string.Individual), farmerIdvData.toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
            }
        }
    }

    public void openCalendar(){
        try{
            Calendar mcurrentDate = Calendar.getInstance();
            year = mcurrentDate.get(Calendar.YEAR);
            month = mcurrentDate.get(Calendar.MONTH);
            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog  mDatePicker =new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                {
                    //ed_date.setText(new StringBuilder().append(year).append("-").append(month+1).append("-").append(day));
                    day = selectedday;
                    month = selectedmonth + 1;
                    year = selectedyear;
                    String sday = String.valueOf(day);
                    sday = (sday.length() == 1)?("0"+sday):sday;
                    String smonth = String.valueOf(month);
                    smonth = (smonth.length() == 1)?("0"+smonth):smonth;
                    input_age.setText(sday + "/" + smonth+ "/" + String.valueOf(year));
                    try {
                        farmerIdvData.put("DOB", input_age.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Individual), farmerIdvData.toString());
                    } catch (JSONException e) {
                        logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
                    }
                }
            },year, month, day);
            mDatePicker.setTitle("Please select date");
            // TODO Hide Future Date Here
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            // TODO Hide Past Date Here
            //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            mDatePicker.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void verifyAadharAndPhone(){
        try{
            if (!validatePhone())
                return;
            if (!validateAadhar())
                return;
            farmerIdvData.put("Aadhar", input_aadhar.getText().toString().trim());
            farmerIdvData.put("Phone", input_phone.getText().toString().trim());
            new  callVerifyAadharAndPhone().execute();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public class callVerifyAadharAndPhone extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loadingaadharverify.setVisibility(View.VISIBLE);
            btn_verifyAadharandphone.setText("");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject response = new JSONObject();
            try {
                response.put("responseCode", 200);
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                pb_loadingaadharverify.setVisibility(View.INVISIBLE);
                btn_verifyAadharandphone.setText("Verify");
                if(result.getInt("responseCode") == 200) {
                    ll_verifyotp.setVisibility(View.VISIBLE);
                    requestFocus(input_otp);
                }
                else {
                    Snackbar.make(null, result.getString("response"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }
    }

    private boolean validatePhone() {
        String val = validations.validatePhone(input_phone.getText().toString().trim());
        if (val != null) {
            input_layout_phone.setError(val);
            requestFocus(input_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAadhar() {
        String val = validations.validateAadhar(input_aadhar.getText().toString().trim());
        if (val != null) {
            input_layout_aadhar.setError(val);
            requestFocus(input_aadhar);
            return false;
        } else {
            input_layout_aadhar.setErrorEnabled(false);
        }
        return true;
    }

    public void verifyOtp(){
        try{
            if (!validateOtp())
                return;
            farmerIdvData.put("Otp", input_otp.getText().toString().trim());
            new  callVerifyOtp().execute();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public class callVerifyOtp extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loadingotp.setVisibility(View.VISIBLE);
            btn_sendotp.setText("");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject response = new JSONObject();
            try {
                response.put("responseCode", 200);
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                pb_loadingotp.setVisibility(View.INVISIBLE);
                btn_sendotp.setText("Send");
                if(result.getInt("responseCode") == 200) {
                    ll_showfarmerphoneandaadhar.setVisibility(View.VISIBLE);
                    ll_verifyfarmerphoneandaadhar.setVisibility(View.GONE);
                    ll_individualform.setVisibility(View.VISIBLE);
                    tv_aadhar.setText("Aadhar :"+farmerIdvData.getString("Aadhar"));
                    tv_phone.setText("Phone :"+farmerIdvData.getString("Phone"));
                    Toast.makeText(getActivity(), "Aadhar and Phone numbers verified", Toast.LENGTH_LONG).show();
                }
                else {
                    Snackbar.make(null, result.getString("response"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }
    }

    private boolean validateOtp() {
        String val = validations.validateOTP(input_otp.getText().toString().trim());
        if (val != null) {
            input_layout_otp.setError(val);
            requestFocus(input_otp);
            return false;
        } else {
            input_layout_otp.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void validateIndividualData(){
        try{
            dbHelper.setParameter(getString(R.string.Individual), farmerIdvData.toString());
            dbHelper.setParameter(getString(R.string.IndividualStatus), "3");
            if(!farmerIdvData.has("Aadhar") && !farmerIdvData.has("Phone")) {
                dbHelper.setParameter(getString(R.string.IndividualStatus), "0");
            }else if(!farmerIdvData.has("FirstName") || farmerIdvData.getString("FirstName").equalsIgnoreCase("")
                    || !farmerIdvData.has("DOB") || farmerIdvData.getString("DOB").equalsIgnoreCase("")
                    || !farmerIdvData.has("Cast") || farmerIdvData.getString("Cast").equalsIgnoreCase("")
                    || !farmerIdvData.has("Gender") || farmerIdvData.getString("Gender").equalsIgnoreCase("")
                    || !farmerIdvData.has("FarmerType") || farmerIdvData.getString("FarmerType").equalsIgnoreCase("")
                    || !farmerIdvData.has("FarmType") || farmerIdvData.getString("FarmType").equalsIgnoreCase("")
                    || !farmerIdvData.has("Address1") || farmerIdvData.getString("Address1").equalsIgnoreCase("")
                    || !farmerIdvData.has("State") || farmerIdvData.getString("State").equalsIgnoreCase("")
                    || !farmerIdvData.has("District") || farmerIdvData.getString("District").equalsIgnoreCase("")
                    || !farmerIdvData.has("Pincode") || farmerIdvData.getString("Pincode").equalsIgnoreCase("")){
                dbHelper.setParameter(getString(R.string.IndividualStatus), "1");
            }else if(!farmerIdvData.has("LastName") || farmerIdvData.getString("LastName").equalsIgnoreCase("")
                    || !farmerIdvData.has("Surname") || farmerIdvData.getString("Surname").equalsIgnoreCase("")
                    || !farmerIdvData.has("Address2") || farmerIdvData.getString("Address2").equalsIgnoreCase("")
                    || !farmerIdvData.has("VillageTown") || farmerIdvData.getString("VillageTown").equalsIgnoreCase("")){
                dbHelper.setParameter(getString(R.string.IndividualStatus), "2");
            }
            goBack();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
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

    @Override
    public void onClick(View v) {
        try {
            boolean checked = false;
            if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();

            switch (v.getId()) {
                case R.id.img_calendar:
                    openCalendar();
                    break;
                case R.id.btn_verifyAadharandphone:
                    verifyAadharAndPhone();
                    break;
                case R.id.btn_sendotp:
                    verifyOtp();
                    break;
                case R.id.btn_individualdatasave:
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    validateIndividualData();
                    break;
                //cast radio button group
                case R.id.rb_cast_general:
                    if (checked)
                        farmerIdvData.put("Cast", "General");
                    break;
                case R.id.rb_cast_obc:
                    if (checked)
                        farmerIdvData.put("Cast", "OBC");
                    break;
                case R.id.rb_cast_sc:
                    if (checked)
                        farmerIdvData.put("Cast", "SC");
                    break;
                case R.id.rb_cast_st:
                    if (checked)
                        farmerIdvData.put("Cast", "ST");
                    break;
                case R.id.rb_cast_other:
                    if (checked)
                        farmerIdvData.put("Cast", "Other");
                    break;

                //Gender radio button group
                case R.id.rb_male:
                    if (checked)
                        farmerIdvData.put("Gender", "Male");
                    break;
                case R.id.rb_female:
                    if (checked)
                        farmerIdvData.put("Gender", "Female");
                    break;

                //Farmer Type radio button group
                case R.id.rb_ftsmall:
                    if (checked)
                        farmerIdvData.put("FarmerType", "Small");
                    break;
                case R.id.rb_ftmarginal:
                    if (checked)
                        farmerIdvData.put("FarmerType", "Marginal");
                    break;
                case R.id.rb_ftother:
                    if (checked)
                        farmerIdvData.put("FarmerType", "Other");
                    break;

                //Farm Type radio button group
                case R.id.rb_fcowner:
                    if (checked)
                        farmerIdvData.put("FarmType", "Owner");
                    break;
                case R.id.rb_fctenant:
                    if (checked)
                        farmerIdvData.put("FarmType", "Tenant");
                    break;
                case R.id.rb_fcsharecropper:
                    if (checked)
                        farmerIdvData.put("FarmType", "ShareCropper");
                    break;
                default:
                    break;
            }
            dbHelper.setParameter(getString(R.string.Individual), farmerIdvData.toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }
}
