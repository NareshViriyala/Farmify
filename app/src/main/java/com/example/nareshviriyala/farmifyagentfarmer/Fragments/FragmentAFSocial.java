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
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterKeyValueSocialInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddKeyValueSocialItem;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.ValidationFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelKeyValueInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAFSocial extends Fragment implements View.OnClickListener {

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    public JSONObject farmersocialData;
    private Validations validations;
    private Button btn_socialdatasave;
    private EditText input_facebookid, input_whatsappnumber, input_srcother, input_rationcard, input_pancard;
    private ListView lv_referencelist;
    private FloatingActionButton fab_addreference;
    private AdapterKeyValueSocialInformation adapterreferenceInformation;

    public FragmentAFSocial(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_af_social, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            validations = new Validations();
            String data = dbHelper.getParameter(getString(R.string.Social));
            if(data.isEmpty() || data == null)
                farmersocialData = new JSONObject();
            else
                farmersocialData = new JSONObject(data);
            ((HomeActivity) getActivity()).setActionBarTitle("Social Details");
            btn_socialdatasave = rootView.findViewById(R.id.btn_socialdatasave);
            btn_socialdatasave.setOnClickListener(this);

            input_facebookid = rootView.findViewById(R.id.input_facebookid);
            input_whatsappnumber = rootView.findViewById(R.id.input_whatsappnumber);
            input_srcother = rootView.findViewById(R.id.input_srcother);
            input_rationcard = rootView.findViewById(R.id.input_rationcard);
            input_pancard = rootView.findViewById(R.id.input_pancard);

            input_facebookid.addTextChangedListener(new MyTextWatcher(input_facebookid));
            input_whatsappnumber.addTextChangedListener(new MyTextWatcher(input_whatsappnumber));
            input_srcother.addTextChangedListener(new MyTextWatcher(input_srcother));
            input_rationcard.addTextChangedListener(new MyTextWatcher(input_rationcard));
            input_pancard.addTextChangedListener(new MyTextWatcher(input_pancard));

            lv_referencelist = rootView.findViewById(R.id.lv_referencelist);

            rootView.findViewById(R.id.rb_referencesyes).setOnClickListener(this);
            rootView.findViewById(R.id.rb_referencesno).setOnClickListener(this);

            fab_addreference = rootView.findViewById(R.id.fab_addreference);
            fab_addreference.setOnClickListener(this);

            rootView.findViewById(R.id.chk_facebook).setOnClickListener(this);
            rootView.findViewById(R.id.chk_whatsapp).setOnClickListener(this);

            rootView.findViewById(R.id.chk_lantelugu).setOnClickListener(this);
            rootView.findViewById(R.id.chk_lanhindi).setOnClickListener(this);
            rootView.findViewById(R.id.chk_lanenglish).setOnClickListener(this);
            rootView.findViewById(R.id.chk_lanother).setOnClickListener(this);

            rootView.findViewById(R.id.chk_srcpaper).setOnClickListener(this);
            rootView.findViewById(R.id.chk_srcao).setOnClickListener(this);
            rootView.findViewById(R.id.chk_srctv).setOnClickListener(this);
            rootView.findViewById(R.id.chk_srcother).setOnClickListener(this);
            //dbHelper.deleteParameter(getString(R.string.Social));
            populateForm();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void populateForm(){
        try{
            if(farmersocialData.has("FacebookID")) {
                input_facebookid.setText(farmersocialData.getString("FacebookID"));
                rootView.findViewById(R.id.input_layout_facebookid).setVisibility(View.VISIBLE);
            }
            if(farmersocialData.has("WhatsappID")) {
                input_whatsappnumber.setText(farmersocialData.getString("WhatsappID"));
                rootView.findViewById(R.id.input_layout_whatsappnumber).setVisibility(View.VISIBLE);
            }
            if(farmersocialData.has("SourceOfInfoOther")) {
                input_srcother.setText(farmersocialData.getString("SourceOfInfoOther"));
                rootView.findViewById(R.id.input_layout_srcother).setVisibility(View.VISIBLE);
            }
            if(farmersocialData.has("RationCard"))
                input_rationcard.setText(farmersocialData.getString("RationCard"));
            if(farmersocialData.has("PanCard"))
                input_pancard.setText(farmersocialData.getString("PanCard"));

            if(farmersocialData.has("Languages")){
                JSONArray langs = farmersocialData.getJSONArray("Languages");
                for(int i = 0; i < langs.length(); i++){
                    String lang = langs.get(i).toString();
                    switch (lang){
                        case "Telugu":
                            ((CheckBox)rootView.findViewById(R.id.chk_lantelugu)).setChecked(true);
                            break;
                        case "Hindi":
                            ((CheckBox)rootView.findViewById(R.id.chk_lanhindi)).setChecked(true);
                            break;
                        case "English":
                            ((CheckBox)rootView.findViewById(R.id.chk_lanenglish)).setChecked(true);
                            break;
                        case "Other":
                            ((CheckBox)rootView.findViewById(R.id.chk_lanother)).setChecked(true);
                            break;
                    }
                }
            }

            if(farmersocialData.has("SourceInformation")){
                JSONArray srcinfo = farmersocialData.getJSONArray("SourceInformation");
                for(int i = 0; i < srcinfo.length(); i++) {
                    String info = srcinfo.get(i).toString();
                    switch (info){
                        case "Paper":
                            ((CheckBox)rootView.findViewById(R.id.chk_srcpaper)).setChecked(true);
                            break;
                        case "Agri Officer":
                            ((CheckBox)rootView.findViewById(R.id.chk_srcao)).setChecked(true);
                            break;
                        case "TV-Radio":
                            ((CheckBox)rootView.findViewById(R.id.chk_srctv)).setChecked(true);
                            break;
                        case "Other":
                            ((CheckBox)rootView.findViewById(R.id.chk_srcother)).setChecked(true);
                            break;
                    }
                }
            }

            if(farmersocialData.has("SocialMediaInformation")){
                JSONArray sminfo = farmersocialData.getJSONArray("SocialMediaInformation");
                for(int i = 0; i < sminfo.length(); i++) {
                    String info = sminfo.get(i).toString();
                    switch (info){
                        case "Facebook":
                            ((CheckBox)rootView.findViewById(R.id.chk_facebook)).setChecked(true);
                            break;
                        case "Whatsapp":
                            ((CheckBox)rootView.findViewById(R.id.chk_whatsapp)).setChecked(true);
                            break;
                    }
                }
            }
            refreshReferenceListView();
            }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshReferenceListView(){
        try{
            if(!farmersocialData.has("ReferenceInformation")){
                rootView.findViewById(R.id.ll_referencelist).setVisibility(View.GONE);
                return;
            }
            ArrayList<ModelKeyValueInformation> referenceInformation = new ArrayList<>();
            JSONArray referenceList = farmersocialData.getJSONArray("ReferenceInformation");
            for(int i = 0; i < referenceList.length(); i++){
                JSONObject item = referenceList.getJSONObject(i);
                referenceInformation.add(new ModelKeyValueInformation(item.getInt("Id"), item.getString("Name"), item.getString("Phone")));
            }
            if(referenceInformation.size() > 0)
                rootView.findViewById(R.id.ll_referencelist).setVisibility(View.VISIBLE);
            else
                rootView.findViewById(R.id.ll_referencelist).setVisibility(View.GONE);

            if(adapterreferenceInformation == null) {
                adapterreferenceInformation = new AdapterKeyValueSocialInformation(this.getContext(), referenceInformation, this, "ReferenceInformation", "Name", "Phone");
                lv_referencelist.setAdapter(adapterreferenceInformation);
            }else
                adapterreferenceInformation.updateAssetList(referenceInformation);
            dbHelper.setParameter(getString(R.string.Social),farmersocialData.toString());
            if(getView() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
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
                    case R.id.input_facebookid:
                        farmersocialData.put("FacebookID", input_facebookid.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
                        break;
                    case R.id.input_whatsappnumber:
                        farmersocialData.put("WhatsappID", input_whatsappnumber.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
                        break;
                    case R.id.input_srcother:
                        farmersocialData.put("SourceOfInfoOther", input_srcother.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
                        break;
                    case R.id.input_rationcard:
                        farmersocialData.put("RationCard", input_rationcard.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
                        break;
                    case R.id.input_pancard:
                        farmersocialData.put("PanCard", input_pancard.getText().toString().trim());
                        dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
                        break;
                }
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        try{
            boolean checked = false;
            if(v instanceof CheckBox)
                checked = ((CheckBox) v).isChecked();
            if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();

            switch (v.getId()){
                case R.id.chk_facebook:
                    modifySocialMediaArray();
                    if(checked)
                        rootView.findViewById(R.id.input_layout_facebookid).setVisibility(View.VISIBLE);
                    else
                        rootView.findViewById(R.id.input_layout_facebookid).setVisibility(View.GONE);
                    break;
                case R.id.chk_whatsapp:
                    modifySocialMediaArray();
                    if(checked)
                        rootView.findViewById(R.id.input_layout_whatsappnumber).setVisibility(View.VISIBLE);
                    else
                        rootView.findViewById(R.id.input_layout_whatsappnumber).setVisibility(View.GONE);
                    break;
                case R.id.chk_lantelugu:
                    modifyLanguageArray();
                    break;
                case R.id.chk_lanhindi:
                    modifyLanguageArray();
                    break;
                case R.id.chk_lanenglish:
                    modifyLanguageArray();
                    break;
                case R.id.chk_lanother:
                    modifyLanguageArray();
                    break;
                case R.id.chk_srcpaper:
                    modifySourceInfoArray();
                    break;
                case R.id.chk_srcao:
                    modifySourceInfoArray();
                    break;
                case R.id.chk_srctv:
                    modifySourceInfoArray();
                    break;
                case R.id.chk_srcother:
                    modifySourceInfoArray();
                    if(checked)
                        rootView.findViewById(R.id.input_layout_srcother).setVisibility(View.VISIBLE);
                    else
                        rootView.findViewById(R.id.input_layout_srcother).setVisibility(View.GONE);
                    break;
                case R.id.rb_referencesyes:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabcontainer).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_referencesno:
                    if (checked)
                        rootView.findViewById(R.id.ll_fabcontainer).setVisibility(View.GONE);
                    break;
                case R.id.fab_addreference:
                    new DialogAddKeyValueSocialItem(getActivity(), null, this, "ReferenceInformation", "Name", "Phone").show();
                    break;
                case R.id.btn_socialdatasave:
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    validateSocialData();
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void modifyLanguageArray(){
        try{
            /*JSONArray langs;
            if(!farmersocialData.has("Languages"))
                langs = new JSONArray();
            else
                langs = farmersocialData.getJSONArray("Languages");

            if(status)
                langs.put(language);
            else{
                String jsonString = langs.toString();
                jsonString = jsonString.replaceAll("\""+language+"\",", "").replaceAll(",\""+language+"\"]", "]");
                langs = new JSONArray(jsonString);
            }*/
            farmersocialData.remove("Languages");
            JSONArray srcInfo = new JSONArray();

            if(((CheckBox)rootView.findViewById(R.id.chk_lantelugu)).isChecked())
                srcInfo.put("Telugu");
            if(((CheckBox)rootView.findViewById(R.id.chk_lanhindi)).isChecked())
                srcInfo.put("Hindi");
            if(((CheckBox)rootView.findViewById(R.id.chk_lanenglish)).isChecked())
                srcInfo.put("English");
            if(((CheckBox)rootView.findViewById(R.id.chk_lanother)).isChecked())
                srcInfo.put("Other");

            if(srcInfo.length() > 0)
                farmersocialData.put("Languages", srcInfo);
            dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void modifySourceInfoArray(){
        try{
            farmersocialData.remove("SourceInformation");
            JSONArray srcInfo = new JSONArray();

            if(((CheckBox)rootView.findViewById(R.id.chk_srcpaper)).isChecked())
                srcInfo.put("Paper");
            if(((CheckBox)rootView.findViewById(R.id.chk_srcao)).isChecked())
                srcInfo.put("Agri Officer");
            if(((CheckBox)rootView.findViewById(R.id.chk_srctv)).isChecked())
                srcInfo.put("TV-Radio");
            if(((CheckBox)rootView.findViewById(R.id.chk_srcother)).isChecked()) {
                srcInfo.put("Other");
                farmersocialData.put("SourceOfInfoOther",farmersocialData.has("SourceOfInfoOther")?farmersocialData.getString("SourceOfInfoOther"):"");
            }
            else
                farmersocialData.remove("SourceOfInfoOther");

            if(srcInfo.length() > 0)
                farmersocialData.put("SourceInformation", srcInfo);
            dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void modifySocialMediaArray(){
        try{
            /*JSONArray socialInfo;
            if(!farmersocialData.has("SocialMediaInformation"))
                socialInfo = new JSONArray();
            else
                socialInfo = farmersocialData.getJSONArray("SocialMediaInformation");

            if(status)
                socialInfo.put(src);
            else{
                String jsonString = socialInfo.toString();
                jsonString = jsonString.replaceAll("\""+src+"\",", "")
                                       .replaceAll(",\""+src+"\"]", "]")
                                       .replaceAll("\""+src+"\"", "[]");
                socialInfo = new JSONArray(jsonString);
                farmersocialData.remove(src+"ID");
            }*/
            farmersocialData.remove("SocialMediaInformation");
            JSONArray srcInfo = new JSONArray();

            if(((CheckBox)rootView.findViewById(R.id.chk_facebook)).isChecked()) {
                srcInfo.put("Facebook");
                farmersocialData.put("FacebookID",farmersocialData.has("FacebookID")?farmersocialData.getString("FacebookID"):"");
            }
            else
                farmersocialData.remove("FacebookID");
            if(((CheckBox)rootView.findViewById(R.id.chk_whatsapp)).isChecked()) {
                srcInfo.put("Whatsapp");
                farmersocialData.put("WhatsappID",farmersocialData.has("WhatsappID")?farmersocialData.getString("WhatsappID"):"");
            }
            else
                farmersocialData.remove("WhatsappID");

            if(srcInfo.length() > 0)
                farmersocialData.put("SocialMediaInformation", srcInfo);
            dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validateSocialData(){
        try{
            dbHelper.setParameter(getString(R.string.Social), farmersocialData.toString());
            dbHelper.setParameter(getString(R.string.SocialStatus), "3");
            new ValidationFarmerData(getActivity()).validateSocialData();
            /*if(!farmersocialData.has("RationCard") || farmersocialData.getString("RationCard").equalsIgnoreCase("")
                    || !farmersocialData.has("PanCard") || farmersocialData.getString("PanCard").equalsIgnoreCase("")
                    || (farmersocialData.has("FacebookID") && farmersocialData.getString("FacebookID").equalsIgnoreCase(""))
                    || (farmersocialData.has("WhatsappID") && farmersocialData.getString("WhatsappID").equalsIgnoreCase(""))
                    || !farmersocialData.has("PanCard") || farmersocialData.getString("PanCard").equalsIgnoreCase("")
                    || !farmersocialData.has("Languages") || farmersocialData.getString("Languages").equalsIgnoreCase("")
                    || !farmersocialData.has("SourceInformation") || farmersocialData.getString("SourceInformation").equalsIgnoreCase("")
                    || !farmersocialData.has("SocialMediaInformation") || farmersocialData.getString("SocialMediaInformation").equalsIgnoreCase("")){
                dbHelper.setParameter(getString(R.string.SocialStatus), "2");
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
}