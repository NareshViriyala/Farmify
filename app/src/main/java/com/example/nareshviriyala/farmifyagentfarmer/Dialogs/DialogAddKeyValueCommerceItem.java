package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentCommerce;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentSocial;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelKeyValueInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class DialogAddKeyValueCommerceItem extends Dialog implements View.OnClickListener{
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    private Context context;
    private ModelKeyValueInformation item;
    private FragmentCommerce fragmentCommerce;
    private TextInputLayout input_layout_assetname, input_layout_assetvalue;
    private EditText input_name, input_value;
    private TextView tv_header;
    private Button btn_assetsave, btn_assetcancel;
    private String jsonKey, key, value;
    private String[] dialogTitles;

    public DialogAddKeyValueCommerceItem(@NonNull Context context, ModelKeyValueInformation item, FragmentCommerce fragmentCommerce, String jsonKey, String key, String value) {
        super(context);
        try {
            this.context = context;
            this.item = item;
            this.jsonKey = jsonKey;
            this.key = key;
            this.value = value;
            logErrors = LogErrors.getInstance(context);
            this.fragmentCommerce = fragmentCommerce;
            int arryid = this.context.getResources().getIdentifier(jsonKey, "array", this.context.getPackageName());
            dialogTitles = context.getResources().getStringArray(arryid);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(context);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_addkeyvalue);

        input_name = (EditText) findViewById(R.id.input_assetname);
        input_value = (EditText) findViewById(R.id.input_assetvalue);
        input_layout_assetname = findViewById(R.id.input_layout_assetname);
        input_layout_assetvalue = findViewById(R.id.input_layout_assetvalue);
        tv_header = findViewById(R.id.tv_header);
        tv_header.setText(dialogTitles[0]);
        input_name.setHint(dialogTitles[1]);
        input_value.setHint(dialogTitles[2]);

        btn_assetsave = findViewById(R.id.btn_assetsave);
        btn_assetcancel = findViewById(R.id.btn_assetcancel);

        btn_assetsave.setOnClickListener(this);
        btn_assetcancel.setOnClickListener(this);
        populateView();
    }

    public void populateView(){
        try{
            if(item != null){
                input_name.setText(item.getAssetName());
                input_value.setText(item.getAssetValue());
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_assetsave:
                    validateAssetDialog();
                    break;
                case R.id.btn_assetcancel:
                    dismiss();
                    break;

            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validateAssetDialog(){
        try{
            if (input_name.getText().toString().trim().isEmpty()) {
                input_layout_assetname.setError("Enter data");
                requestFocus(input_name);
                return;
            }else
                input_layout_assetname.setErrorEnabled(false);

            if (input_value.getText().toString().trim().isEmpty()) {
                input_layout_assetvalue.setError("Enter data");
                requestFocus(input_value);
                return;
            }else
                input_layout_assetvalue.setErrorEnabled(false);

            JSONArray assetList;
            if(fragmentCommerce.farmercommerceData.has(jsonKey))
                assetList = fragmentCommerce.farmercommerceData.getJSONArray(jsonKey);
            else
                assetList = new JSONArray();

            if(item == null) { //new insert
                int Id = 0;
                for(int i = 0; i < assetList.length(); i++){
                    Id = (Id < assetList.getJSONObject(i).getInt("Id"))?assetList.getJSONObject(i).getInt("Id"):Id;
                }
                Id = Id == 0 ? 1 : Id + 1;
                JSONObject jobj = new JSONObject();
                jobj.put("Id", Id);
                jobj.put(key, input_name.getText().toString().trim());
                jobj.put(value, input_value.getText().toString().trim());
                assetList.put(jobj);
            }else{ //editing existing item
                JSONArray newAssetList = new JSONArray();
                for(int i = 0; i < assetList.length(); i++){
                    JSONObject jobj = assetList.getJSONObject(i);
                    if(jobj.getInt("Id") == item.getId()){
                        jobj.put(key, input_name.getText().toString().trim());
                        jobj.put(value, input_value.getText().toString().trim());
                    }
                    newAssetList.put(jobj);
                }
                assetList = newAssetList;
            }
            fragmentCommerce.farmercommerceData.put(jsonKey, assetList);
            if(jsonKey.equalsIgnoreCase("AssetInformation"))
                fragmentCommerce.refreshAssetListView();
            if(jsonKey.equalsIgnoreCase("OsiInformation"))
                fragmentCommerce.refreshOsiListView();
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
