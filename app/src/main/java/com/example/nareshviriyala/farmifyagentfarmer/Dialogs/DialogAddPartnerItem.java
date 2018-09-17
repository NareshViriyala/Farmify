package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFPartner;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelPartnerInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class DialogAddPartnerItem extends Dialog implements View.OnClickListener{
    private LogErrors logErrors;
    private String className;
    private Context context;
    private ModelPartnerInformation item;
    private FragmentAFPartner fragmentPartner;
    private TextInputLayout input_layout_partnername, input_layout_partnerphone;
    private EditText input_name, input_value;
    private Button btn_partnersave, btn_partnercancel;
    private Spinner spnr_partnertype;

    public DialogAddPartnerItem(@NonNull Context context, ModelPartnerInformation item, FragmentAFPartner fragmentPartner) {
        super(context);
        try {
            this.context = context;
            this.item = item;
            logErrors = LogErrors.getInstance(context);
            this.fragmentPartner = fragmentPartner;
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_addpartner);

        input_name = (EditText) findViewById(R.id.input_partnername);
        input_value = (EditText) findViewById(R.id.input_partnerphone);
        input_layout_partnername = findViewById(R.id.input_layout_partnername);
        input_layout_partnerphone = findViewById(R.id.input_layout_partnerphone);
        spnr_partnertype = findViewById(R.id.spnr_partnertype);

        btn_partnersave = findViewById(R.id.btn_partnersave);
        btn_partnercancel = findViewById(R.id.btn_partnercancel);

        btn_partnersave.setOnClickListener(this);
        btn_partnercancel.setOnClickListener(this);
        populateView();
    }

    public void populateView(){
        try{
            if(item != null){
                input_name.setText(item.getPartnerName());
                input_value.setText(item.getPartnerPhone());
                String[] partnerType = context.getResources().getStringArray(R.array.partners_type);
                int index = Arrays.asList(partnerType).indexOf(item.getPartnerType());
                spnr_partnertype.setSelection(index, true);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_partnersave:
                    validatePartnerDialog();
                    break;
                case R.id.btn_partnercancel:
                    dismiss();
                    break;

            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validatePartnerDialog(){
        try{
            if (input_name.getText().toString().trim().isEmpty()) {
                input_layout_partnername.setError("Enter data");
                requestFocus(input_name);
                return;
            }else
                input_layout_partnername.setErrorEnabled(false);

            if (input_value.getText().toString().trim().isEmpty()) {
                input_layout_partnerphone.setError("Enter data");
                requestFocus(input_value);
                return;
            }else
                input_layout_partnerphone.setErrorEnabled(false);

            if(spnr_partnertype.getSelectedItem().toString().equalsIgnoreCase("Select")){
                ((TextView)spnr_partnertype.getSelectedView()).setError("Select partner type");
                return;
            }

            JSONArray partnerList;
            if(fragmentPartner.farmerpartnerData.length() > 0)
                partnerList = fragmentPartner.farmerpartnerData;
            else
                partnerList = new JSONArray();

            if(item == null) { //new insert
                int Id = 0;
                for(int i = 0; i < partnerList.length(); i++){
                    Id = (Id < partnerList.getJSONObject(i).getInt("Id"))?partnerList.getJSONObject(i).getInt("Id"):Id;
                }
                Id = Id == 0 ? 1 : Id + 1;
                JSONObject jobj = new JSONObject();
                jobj.put("Id", Id);
                jobj.put("PartnerName", input_name.getText().toString().trim());
                jobj.put("PartnerPhone", input_value.getText().toString().trim());
                jobj.put("PartnerType", spnr_partnertype.getSelectedItem().toString().trim());
                partnerList.put(jobj);
            }else{ //editing existing item
                JSONArray newAssetList = new JSONArray();
                for(int i = 0; i < partnerList.length(); i++){
                    JSONObject jobj = partnerList.getJSONObject(i);
                    if(jobj.getInt("Id") == item.getId()){
                        jobj.put("PartnerName", input_name.getText().toString().trim());
                        jobj.put("PartnerPhone", input_value.getText().toString().trim());
                        jobj.put("PartnerType", spnr_partnertype.getSelectedItem().toString().trim());
                    }
                    newAssetList.put(jobj);
                }
                partnerList = newAssetList;
            }
            fragmentPartner.farmerpartnerData = partnerList;
            fragmentPartner.refreshPartnerListView();
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
