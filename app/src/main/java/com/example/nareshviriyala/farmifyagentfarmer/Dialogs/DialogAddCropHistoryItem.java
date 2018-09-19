package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFAgronomic;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFAgronomicSlave;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFSocial;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCropHistoryInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelKeyValueInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DialogAddCropHistoryItem extends Dialog implements View.OnClickListener{
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private Context context;
    private ModelCropHistoryInformation item;
    private Spinner spnr_chyear, spnr_chmonth;
    private TextInputLayout input_layout_chacers, input_layout_chcrop, input_layout_chproduction, input_layout_chrate;
    private EditText input_chacers, input_chcrop, input_chproduction, input_chrate;
    private Button btn_crophistorysave, btn_crophistorycancel;
    private List<String> years;
    private FragmentAFAgronomicSlave fragmentAFAgronomicSlave;

    public DialogAddCropHistoryItem(@NonNull Context context, ModelCropHistoryInformation item, FragmentAFAgronomicSlave fragmentAFAgronomicSlave) {
        super(context);
        try {
            this.context = context;
            this.item = item;
            this.fragmentAFAgronomicSlave = fragmentAFAgronomicSlave;
            logErrors = LogErrors.getInstance(context);
            className = new Object() {}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(context);
            years = new ArrayList<>();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_crophistory);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        years.add("Select");
        for(int i = 0; i < 8; i++)
            years.add(String.valueOf(year-i));

        input_chacers = (EditText) findViewById(R.id.input_chacers);
        input_chcrop = (EditText) findViewById(R.id.input_chcrop);
        input_chproduction = (EditText) findViewById(R.id.input_chproduction);
        input_chrate = (EditText) findViewById(R.id.input_chrate);

        input_layout_chacers = findViewById(R.id.input_layout_chacers);
        input_layout_chcrop = findViewById(R.id.input_layout_chcrop);
        input_layout_chproduction = findViewById(R.id.input_layout_chproduction);
        input_layout_chrate = findViewById(R.id.input_layout_chrate);

        spnr_chyear = findViewById(R.id.spnr_chyear);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, years);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
        spnr_chyear.setAdapter(spinnerArrayAdapter);

        spnr_chmonth = findViewById(R.id.spnr_chmonth);

        btn_crophistorysave = findViewById(R.id.btn_crophistorysave);
        btn_crophistorycancel = findViewById(R.id.btn_crophistorycancel);

        btn_crophistorysave.setOnClickListener(this);
        btn_crophistorycancel.setOnClickListener(this);


        populateView();
    }

    public void populateView(){
        try{
            if(item != null){
                input_chacers.setText(item.getAcers());
                input_chcrop.setText(item.getCrop());
                input_chproduction.setText(item.getProduction());
                input_chrate.setText(item.getRate());

                String[] months = context.getResources().getStringArray(R.array.month);
                int monthindex = Arrays.asList(months).indexOf(item.getMonth());
                spnr_chmonth.setSelection(monthindex, true);

                int yearindex = years.indexOf(item.getYear());
                spnr_chyear.setSelection(yearindex, true);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_crophistorysave:
                    validateCropHistoryDialog();
                    break;
                case R.id.btn_crophistorycancel:
                    dismiss();
                    break;

            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validateCropHistoryDialog(){
        try{
            if (input_chacers.getText().toString().trim().isEmpty()) {
                input_layout_chacers.setError("Enter data");
                requestFocus(input_chacers);
                return;
            }else
                input_layout_chacers.setErrorEnabled(false);

            if (input_chcrop.getText().toString().trim().isEmpty()) {
                input_layout_chcrop.setError("Enter data");
                requestFocus(input_chcrop);
                return;
            }else
                input_layout_chcrop.setErrorEnabled(false);

            if (input_chproduction.getText().toString().trim().isEmpty()) {
                input_layout_chproduction.setError("Enter data");
                requestFocus(input_chproduction);
                return;
            }else
                input_layout_chproduction.setErrorEnabled(false);

            if (input_chrate.getText().toString().trim().isEmpty()) {
                input_layout_chrate.setError("Enter data");
                requestFocus(input_chrate);
                return;
            }else
                input_layout_chrate.setErrorEnabled(false);

            if(spnr_chyear.getSelectedItem().toString().trim().equalsIgnoreCase("Select")){
                ((TextView)spnr_chyear.getSelectedView()).setError("Select year");
                return;
            }

            if(spnr_chmonth.getSelectedItem().toString().trim().equalsIgnoreCase("Select")){
                ((TextView)spnr_chmonth.getSelectedView()).setError("Select month");
                return;
            }
            JSONArray cropList;
            if(fragmentAFAgronomicSlave.farmeragronomicDataItem.has("CropHistory"))
                cropList = fragmentAFAgronomicSlave.farmeragronomicDataItem.getJSONArray("CropHistory");
            else
                cropList = new JSONArray();

            if(item == null) { //new insert
                int Id = 0;
                for(int i = 0; i < cropList.length(); i++){
                    Id = (Id < cropList.getJSONObject(i).getInt("Id"))?cropList.getJSONObject(i).getInt("Id"):Id;
                }
                Id = Id == 0 ? 1 : Id + 1;
                JSONObject jobj = new JSONObject();
                jobj.put("Id", Id);
                jobj.put("Year", spnr_chyear.getSelectedItem().toString().trim());
                jobj.put("Month", spnr_chmonth.getSelectedItem().toString().trim());
                jobj.put("Acers", input_chacers.getText().toString().trim());
                jobj.put("Crop", input_chcrop.getText().toString().trim());
                jobj.put("Production", input_chproduction.getText().toString().trim());
                jobj.put("Rate", input_chrate.getText().toString().trim());
                cropList.put(jobj);
            }else{ //editing existing item
                JSONArray newAssetList = new JSONArray();
                for(int i = 0; i < cropList.length(); i++){
                    JSONObject jobj = cropList.getJSONObject(i);
                    if(jobj.getInt("Id") == item.getId()){
                        jobj.put("Year", spnr_chyear.getSelectedItem().toString().trim());
                        jobj.put("Month", spnr_chmonth.getSelectedItem().toString().trim());
                        jobj.put("Acers", input_chacers.getText().toString().trim());
                        jobj.put("Crop", input_chcrop.getText().toString().trim());
                        jobj.put("Production", input_chproduction.getText().toString().trim());
                        jobj.put("Rate", input_chrate.getText().toString().trim());
                    }
                    newAssetList.put(jobj);
                }
                cropList = newAssetList;
            }
            fragmentAFAgronomicSlave.farmeragronomicDataItem.put("CropHistory", cropList);
            fragmentAFAgronomicSlave.refreshCropHistoryListView();
            dismiss();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}
