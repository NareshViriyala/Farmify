package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentCommerce;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class DialogAddCreditItem extends Dialog implements View.OnClickListener{

    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private Context context;
    private EditText input_creditsrcname, input_creditdate, input_creditamount, input_creditinterest, input_creditpending, input;
    private TextInputLayout input_layout_creditpending, input_layout_creditsrcname, input_layout_creditdate, input_layout_creditamount, input_layout_creditinterest;
    private int day, month, year;
    private JSONObject farmercommerceData;
    private RadioButton rb_creditpaidfull, rb_creditpending;
    private ModelCreditInformation item;
    private FragmentCommerce fragmentCommerce;

    public DialogAddCreditItem(@NonNull Context context, ModelCreditInformation item, FragmentCommerce fragmentCommerce) {
        super(context);
        try {
            this.context = context;
            this.item = item;
            logErrors = LogErrors.getInstance(context);
            this.fragmentCommerce = fragmentCommerce;
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(context);
            String data = dbHelper.getParameter(context.getString(R.string.Commerce));
            if (data.isEmpty() || data == null)
                farmercommerceData = new JSONObject();
            else
                farmercommerceData = new JSONObject(data);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_farmer_credit);

        input_creditsrcname = (EditText) findViewById(R.id.input_creditsrcname);
        input_creditdate = (EditText) findViewById(R.id.input_creditdate);
        input_creditamount = (EditText) findViewById(R.id.input_creditamount);
        input_creditinterest = (EditText) findViewById(R.id.input_creditinterest);
        input_creditpending = (EditText) findViewById(R.id.input_creditpending);
        input_layout_creditpending = findViewById(R.id.input_layout_creditpending);
        input_layout_creditsrcname = findViewById(R.id.input_layout_creditsrcname);
        input_layout_creditdate = findViewById(R.id.input_layout_creditdate);
        input_layout_creditamount = findViewById(R.id.input_layout_creditamount);
        input_layout_creditinterest = findViewById(R.id.input_layout_creditinterest);



        ImageView img_credittakencalendar = (ImageView) findViewById(R.id.img_credittakencalendar);
        rb_creditpaidfull = (RadioButton) findViewById(R.id.rb_creditpaidfull);
        rb_creditpending = (RadioButton) findViewById(R.id.rb_creditpending);

        Button btn_creditsave = findViewById(R.id.btn_creditsave);
        Button btn_creditcancel = findViewById(R.id.btn_creditcancel);

        img_credittakencalendar.setOnClickListener(this);
        btn_creditsave.setOnClickListener(this);
        btn_creditcancel.setOnClickListener(this);
        rb_creditpaidfull.setOnClickListener(this);
        rb_creditpending.setOnClickListener(this);
        populateView();

    }

     public void populateView(){
        try{
            if(item == null){
                input_creditsrcname.setText("");
                input_creditdate.setText("");
                input_creditamount.setText("");
                input_creditinterest.setText("");
                input_creditpending.setText("");
            }else{
                input_creditsrcname.setText(item.getSource());
                input_creditdate.setText(item.getDate());
                input_creditamount.setText(item.getAmount());
                input_creditinterest.setText(item.getInterest());
                input_creditpending.setText(item.getPendingAmount());
                if(item.getPaid())
                    ((RadioGroup)findViewById(R.id.rg_creditstatus)).check(R.id.rb_creditpaidfull);
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            boolean checked = false;
            if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();

            switch (v.getId()){
                case R.id.rb_creditpaidfull:
                    if(checked)
                        input_layout_creditpending.setVisibility(View.GONE);
                    break;
                case R.id.rb_creditpending:
                    if(checked)
                        input_layout_creditpending.setVisibility(View.VISIBLE);
                    break;
                case R.id.img_credittakencalendar:
                    openCalendar();
                    break;
                case R.id.btn_creditsave:
                    validateCreditDialog();

                    break;
                case R.id.btn_creditcancel:
                    dismiss();
                    break;

            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validateCreditDialog(){
        try{
            if (input_creditsrcname.getText().toString().trim().isEmpty()) {
                input_layout_creditsrcname.setError("Enter source name");
                requestFocus(input_creditsrcname);
                return;
            }else
                input_layout_creditsrcname.setErrorEnabled(false);

            if (input_creditdate.getText().toString().trim().isEmpty()) {
                input_layout_creditdate.setError("Enter date");
                requestFocus(input_creditdate);
                return;
            }else
                input_layout_creditdate.setErrorEnabled(false);

            if (input_creditamount.getText().toString().trim().isEmpty()) {
                input_layout_creditamount.setError("Enter amount");
                requestFocus(input_creditamount);
                return;
            }
            else
                input_layout_creditamount.setErrorEnabled(false);

            if (input_creditinterest.getText().toString().trim().isEmpty()) {
                input_layout_creditinterest.setError("Enter interest rate");
                requestFocus(input_creditinterest);
                return;
            }else
                input_layout_creditinterest.setErrorEnabled(false);

            if (input_layout_creditpending.getVisibility() == View.VISIBLE && input_creditpending.getText().toString().trim().isEmpty()) {
                input_layout_creditpending.setError("Enter amount");
                requestFocus(input_creditpending);
                return;
            }else
                input_layout_creditpending.setErrorEnabled(false);

            JSONArray jlist;
            if(farmercommerceData.has("CreditInformation"))
                jlist = farmercommerceData.getJSONArray("CreditInformation");
            else
                jlist = new JSONArray();

            if(item == null) { //new insert
                int Id = 0;
                for(int i = 0; i < jlist.length(); i++){
                    Id = (Id < jlist.getJSONObject(i).getInt("Id"))?jlist.getJSONObject(i).getInt("Id"):Id;
                }
                Id = Id == 0 ? 1 : Id + 1;
                JSONObject jobj = new JSONObject();
                jobj.put("Id", Id);
                jobj.put("Source", input_creditsrcname.getText().toString().trim());
                jobj.put("Date", input_creditdate.getText().toString().trim());
                jobj.put("Amount", input_creditamount.getText().toString().trim());
                jobj.put("Interest", input_creditinterest.getText().toString().trim());
                jobj.put("Paid", rb_creditpaidfull.isSelected());
                jobj.put("PendingAmount", input_creditpending.getText().toString().trim());
                jlist.put(jobj);
            }else{ //editing existing item
                JSONArray newlist = new JSONArray();
                for(int i = 0; i < jlist.length(); i++){
                    JSONObject jobj = jlist.getJSONObject(i);
                    if(jobj.getInt("Id") == item.getId()){
                        jobj.put("Source", input_creditsrcname.getText().toString().trim());
                        jobj.put("Date", input_creditdate.getText().toString().trim());
                        jobj.put("Amount", input_creditamount.getText().toString().trim());
                        jobj.put("Interest", input_creditinterest.getText().toString().trim());
                        jobj.put("Paid", rb_creditpaidfull.isSelected());
                        jobj.put("PendingAmount", input_creditpending.getText().toString().trim());
                    }
                    newlist.put(jobj);
                }
                jlist = newlist;
            }
            farmercommerceData.put("CreditInformation", jlist);
            dbHelper.setParameter(context.getString(R.string.Commerce), farmercommerceData.toString());
            fragmentCommerce.refreshCreditListView();
            dismiss();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void openCalendar(){
        try{
            Calendar mcurrentDate = Calendar.getInstance();
            year = mcurrentDate.get(Calendar.YEAR);
            month = mcurrentDate.get(Calendar.MONTH);
            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker =new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener()
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
                    input_creditdate.setText(sday + "/" + smonth+ "/" + String.valueOf(year));
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

}
