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
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterKeyValueCommerceInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddKeyValueCommerceItem;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddCreditItem;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.ValidationFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelKeyValueInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAFCommerce extends Fragment implements View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    public JSONObject farmercommerceData;
    private Validations validations;
    private Button btn_commercedatasave;
    private EditText input_annualincome, input_incomefromcrops, input_feother;
    private FloatingActionButton fab_addcredit, fab_addasset, fab_addosi;
    private AdapterCreditInformation adapterCreditInformation;
    private AdapterKeyValueCommerceInformation adapterAssetInformation, adapterOsiInformation;
    private ListView lv_creditlist, lv_assetlist, lv_osilist;

    public FragmentAFCommerce(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_af_commerce, container, false);

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
            lv_assetlist = rootView.findViewById(R.id.lv_assetlist);
            lv_osilist = rootView.findViewById(R.id.lv_osilist);

            input_annualincome.addTextChangedListener(new MyTextWatcher(input_annualincome));
            input_incomefromcrops.addTextChangedListener(new MyTextWatcher(input_incomefromcrops));
            input_feother.addTextChangedListener(new MyTextWatcher(input_feother));

            rootView.findViewById(R.id.chk_febankloan).setOnClickListener(this);
            rootView.findViewById(R.id.chk_femoneylender).setOnClickListener(this);
            rootView.findViewById(R.id.chk_fecother).setOnClickListener(this);

            rootView.findViewById(R.id.rb_credittakenyes).setOnClickListener(this);
            rootView.findViewById(R.id.rb_credittakenno).setOnClickListener(this);

            rootView.findViewById(R.id.rb_assetsyes).setOnClickListener(this);
            rootView.findViewById(R.id.rb_assetsno).setOnClickListener(this);

            rootView.findViewById(R.id.rb_osiyes).setOnClickListener(this);
            rootView.findViewById(R.id.rb_osino).setOnClickListener(this);

            fab_addcredit = rootView.findViewById(R.id.fab_addcredit);
            fab_addcredit.setOnClickListener(this);

            fab_addasset = rootView.findViewById(R.id.fab_addasset);
            fab_addasset.setOnClickListener(this);

            fab_addosi = rootView.findViewById(R.id.fab_addosi);
            fab_addosi.setOnClickListener(this);

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
                rootView.findViewById(R.id.input_layout_feother).setVisibility(View.VISIBLE);
            }

            refreshCreditListView();
            refreshAssetListView();
            refreshOsiListView();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshCreditListView(){
        try{
            if(!farmercommerceData.has("CreditInformation")){
                rootView.findViewById(R.id.ll_creditlist).setVisibility(View.GONE);
                return;
            }
            ArrayList<ModelCreditInformation> creditInformation = new ArrayList<>();
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
            dbHelper.setParameter(getString(R.string.Commerce),farmercommerceData.toString());
            if(getView() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshAssetListView(){
        try{
            if(!farmercommerceData.has("AssetInformation")){
                rootView.findViewById(R.id.ll_assetlist).setVisibility(View.GONE);
                return;
            }
            ArrayList<ModelKeyValueInformation> assetInformation = new ArrayList<>();
            JSONArray assetList = farmercommerceData.getJSONArray("AssetInformation");
            for(int i = 0; i < assetList.length(); i++){
                JSONObject item = assetList.getJSONObject(i);
                assetInformation.add(new ModelKeyValueInformation(item.getInt("Id"), item.getString("AssetName"), item.getString("AssetValue")));
            }
            if(assetInformation.size() > 0)
                rootView.findViewById(R.id.ll_assetlist).setVisibility(View.VISIBLE);
            else
                rootView.findViewById(R.id.ll_assetlist).setVisibility(View.GONE);

            if(adapterAssetInformation == null) {
                adapterAssetInformation = new AdapterKeyValueCommerceInformation(this.getContext(), assetInformation, this, "AssetInformation", "AssetName", "AssetValue");
                lv_assetlist.setAdapter(adapterAssetInformation);
            }else
                adapterAssetInformation.updateAssetList(assetInformation);
            dbHelper.setParameter(getString(R.string.Commerce),farmercommerceData.toString());
            if(getView() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshOsiListView(){
        try{
            if(!farmercommerceData.has("OsiInformation")){
                rootView.findViewById(R.id.ll_osilist).setVisibility(View.GONE);
                return;
            }
            ArrayList<ModelKeyValueInformation> osiInformation = new ArrayList<>();
            JSONArray osiList = farmercommerceData.getJSONArray("OsiInformation");
            for(int i = 0; i < osiList.length(); i++){
                JSONObject item = osiList.getJSONObject(i);
                osiInformation.add(new ModelKeyValueInformation(item.getInt("Id"), item.getString("OsiName"), item.getString("OsiValue")));
            }
            if(osiInformation.size() > 0)
                rootView.findViewById(R.id.ll_osilist).setVisibility(View.VISIBLE);
            else
                rootView.findViewById(R.id.ll_osilist).setVisibility(View.GONE);

            if(adapterOsiInformation == null) {
                adapterOsiInformation = new AdapterKeyValueCommerceInformation(this.getContext(), osiInformation, this, "OsiInformation", "OsiName", "OsiValue");
                lv_osilist.setAdapter(adapterOsiInformation);
            }else
                adapterOsiInformation.updateAssetList(osiInformation);
            dbHelper.setParameter(getString(R.string.Commerce),farmercommerceData.toString());
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
                case R.id.rb_assetsyes:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabassetcontainer).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_assetsno:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabassetcontainer).setVisibility(View.GONE);
                    break;
                case R.id.rb_osiyes:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabosicontainer).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_osino:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabosicontainer).setVisibility(View.GONE);
                    break;
                case R.id.fab_addcredit:
                    new DialogAddCreditItem(getActivity(), null, this).show();
                    break;
                case R.id.fab_addasset:
                    new DialogAddKeyValueCommerceItem(getActivity(), null, this, "AssetInformation", "AssetName", "AssetValue").show();
                    break;
                case R.id.fab_addosi:
                    new DialogAddKeyValueCommerceItem(getActivity(), null, this, "OsiInformation", "OsiName", "OsiValue").show();
                    break;
                case R.id.btn_commercedatasave:
                    validateCommerceData();
                    break;

            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

    }

    public void validateCommerceData(){
        try{
            dbHelper.setParameter(getString(R.string.Commerce), farmercommerceData.toString());
            new ValidationFarmerData(getActivity()).validateCommerceData();

            /*dbHelper.setParameter(getString(R.string.CommerceStatus), "3");

            if(!farmercommerceData.has("AnnualIncome") || farmercommerceData.getString("CropIncome").equalsIgnoreCase("")
                    || !farmercommerceData.has("FarmExpenseSource")
                    || (farmercommerceData.has("FarmExpenseSource") && farmercommerceData.getString("FarmExpenseSource").equalsIgnoreCase(""))){
                dbHelper.setParameter(getString(R.string.CommerceStatus), "1");
            }else if((farmercommerceData.has("CreditInformation") && farmercommerceData.getString("CreditInformation").equalsIgnoreCase(""))
                    || (farmercommerceData.has("AssetInformation") && farmercommerceData.getString("AssetInformation").equalsIgnoreCase(""))
                    || (farmercommerceData.has("ReferenceInformation") && farmercommerceData.getString("ReferenceInformation").equalsIgnoreCase(""))){
                dbHelper.setParameter(getString(R.string.CommerceStatus), "2");
            }*/
            goBack();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
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