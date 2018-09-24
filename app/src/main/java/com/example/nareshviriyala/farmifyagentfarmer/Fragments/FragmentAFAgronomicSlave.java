package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddCropHistoryItem;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.GlobalVariables;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.MiscMethods;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCropHistoryInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class FragmentAFAgronomicSlave extends Fragment implements View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private String className;
    private FloatingActionButton fab_addcrophistory;
    private DatabaseHelper dbHelper;
    public JSONObject farmeragronomicDataItem;
    private JSONArray farmeragronomicData;
    private EditText input_landacers, input_farmingexperience, input_soilother, input_tocother;
    private TextInputLayout input_layout_landacers, input_layout_farmingexperience, input_layout_soilother, input_layout_tocother;
    private Button btn_agronomicslavedatasave;
    private Spinner spnr_watersource;
    private TableLayout tl_crophistory;
    int currentItemId = 0;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1253;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private GlobalVariables globalVariables;

    public FragmentAFAgronomicSlave(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_af_agronomic_slave, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Agronomic Item");
            dbHelper = new DatabaseHelper(getActivity());

            String data = dbHelper.getParameter(getString(R.string.Agronomic));
            if(data.isEmpty() || data == null || data.equalsIgnoreCase("[]"))
                farmeragronomicData = new JSONArray();
            else
                farmeragronomicData = new JSONArray(data);

            globalVariables = GlobalVariables.getInstance();

            //check if this fragment is loaded on backpress or from FragmentAFAgronomic
            if(globalVariables.getagronomicDataItemId() != -2) {
                currentItemId = globalVariables.getagronomicDataItemId();
                globalVariables.setagronomicDataItemId(-2);
            }
            else
                currentItemId = getArguments().getInt("ItemId");

            //check if its a new insert
            if(currentItemId == -1)
                farmeragronomicDataItem = new JSONObject();
            else
                farmeragronomicDataItem = farmeragronomicData.getJSONObject(currentItemId);

            spnr_watersource = rootView.findViewById(R.id.spnr_watersource);
            btn_agronomicslavedatasave = rootView.findViewById(R.id.btn_agronomicslavedatasave);
            btn_agronomicslavedatasave.setOnClickListener(this);
            input_layout_landacers = rootView.findViewById(R.id.input_layout_landacers);
            input_layout_farmingexperience = rootView.findViewById(R.id.input_layout_farmingexperience);
            input_layout_soilother = rootView.findViewById(R.id.input_layout_soilother);
            input_layout_tocother = rootView.findViewById(R.id.input_layout_tocother);

            input_landacers = rootView.findViewById(R.id.input_landacers);
            input_farmingexperience = rootView.findViewById(R.id.input_farmingexperience);
            input_soilother = rootView.findViewById(R.id.input_soilother);
            input_tocother = rootView.findViewById(R.id.input_tocother);

            input_landacers.addTextChangedListener(new MyTextWatcher(input_landacers));
            input_farmingexperience.addTextChangedListener(new MyTextWatcher(input_farmingexperience));
            input_soilother.addTextChangedListener(new MyTextWatcher(input_soilother));
            input_tocother.addTextChangedListener(new MyTextWatcher(input_tocother));

            fab_addcrophistory = rootView.findViewById(R.id.fab_addcrophistory);
            fab_addcrophistory.setOnClickListener(this);

            tl_crophistory = rootView.findViewById(R.id.tl_crophistory);

            (rootView.findViewById(R.id.rb_ftsmall)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_ftmarginal)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_ftother)).setOnClickListener(this);

            (rootView.findViewById(R.id.rb_fcowner)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_fctenant)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_fcsharecropper)).setOnClickListener(this);

            (rootView.findViewById(R.id.chk_toccotton)).setOnClickListener(this);
            (rootView.findViewById(R.id.chk_toccorn)).setOnClickListener(this);
            (rootView.findViewById(R.id.chk_tocchilli)).setOnClickListener(this);
            (rootView.findViewById(R.id.chk_tocother)).setOnClickListener(this);

            (rootView.findViewById(R.id.rb_soilregadi)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_soilnallaregadi)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_soilother)).setOnClickListener(this);

            (rootView.findViewById(R.id.rb_soiltestingyes)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_soiltestingno)).setOnClickListener(this);

            (rootView.findViewById(R.id.rb_cropinsuranceyes)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_cropinsurancegno)).setOnClickListener(this);

            (rootView.findViewById(R.id.rb_crophistoryyes)).setOnClickListener(this);
            (rootView.findViewById(R.id.rb_crophistoryno)).setOnClickListener(this);
            if(currentItemId != -1) {
                fillForm();
                refreshCropHistoryListView();
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void fillForm(){
        try{
            if(farmeragronomicDataItem.has("FarmerType")){

                RadioGroup rg_type = rootView.findViewById(R.id.rg_farmertype);
                switch (farmeragronomicDataItem.getString("FarmerType")){
                    case "Small":
                        rg_type.check(R.id.rb_ftsmall);
                        break;
                    case "Marginal":
                        rg_type.check(R.id.rb_ftmarginal);
                        break;
                    case "Other":
                        rg_type.check(R.id.rb_ftother);
                        break;
                }
            }

            if(farmeragronomicDataItem.has("FarmerCategory")){
                RadioGroup rg_type = rootView.findViewById(R.id.rg_farmcategory);
                switch (farmeragronomicDataItem.getString("FarmerCategory")){
                    case "Owner":
                        rg_type.check(R.id.rb_fcowner);
                        break;
                    case "Tenant":
                        rg_type.check(R.id.rb_fctenant);
                        break;
                    case "SharedCropper":
                        rg_type.check(R.id.rb_fcsharecropper);
                        break;
                }
            }

            if(farmeragronomicDataItem.has("CropType")){
                JSONArray cropTypeList = farmeragronomicDataItem.getJSONArray("CropType");
                for(int i = 0; i < cropTypeList.length(); i++){
                    switch (cropTypeList.getString(i)){
                        case "Cotton":
                            ((CheckBox)(rootView.findViewById(R.id.chk_toccotton))).setChecked(true);
                            break;
                        case "Chilli":
                            ((CheckBox)(rootView.findViewById(R.id.chk_tocchilli))).setChecked(true);
                            break;
                        case "Corn":
                            ((CheckBox)(rootView.findViewById(R.id.chk_toccorn))).setChecked(true);
                            break;
                        case "Other":
                            ((CheckBox)(rootView.findViewById(R.id.chk_tocother))).setChecked(true);
                            break;
                    }
                }
            }

            if(farmeragronomicDataItem.has("CropTypeOther")){
                input_layout_tocother.setVisibility(View.VISIBLE);
                input_tocother.setText(farmeragronomicDataItem.getString("CropTypeOther"));
            }

            if(farmeragronomicDataItem.has("SoilType")){
                RadioGroup rg_type = rootView.findViewById(R.id.rg_soiltype);
                switch (farmeragronomicDataItem.getString("SoilType")){
                    case "Regadi":
                        rg_type.check(R.id.rb_soilregadi);
                        break;
                    case "NallaRegadi":
                        rg_type.check(R.id.rb_soilnallaregadi);
                        break;
                    case "Other":
                        rg_type.check(R.id.rb_soilother);
                        break;
                }
            }

            if(farmeragronomicDataItem.has("SoilTypeOther")){
                input_layout_soilother.setVisibility(View.VISIBLE);
                input_soilother.setText(farmeragronomicDataItem.getString("SoilTypeOther"));
            }

            if(farmeragronomicDataItem.has("WaterSource")){
                String[] wsType = getActivity().getResources().getStringArray(R.array.watersource);
                int index = Arrays.asList(wsType).indexOf(farmeragronomicDataItem.getString("WaterSource"));
                spnr_watersource.setSelection(index, true);
            }

            if(farmeragronomicDataItem.has("LandAcers")){
                input_landacers.setText(farmeragronomicDataItem.getString("LandAcers"));
            }

            if(farmeragronomicDataItem.has("SoilTesting")){
                RadioGroup rg_type = rootView.findViewById(R.id.rg_soiltesting);
                if (farmeragronomicDataItem.getBoolean("SoilTesting"))
                    rg_type.check(R.id.rb_soiltestingyes);
                else
                    rg_type.check(R.id.rb_soiltestingno);
            }

            if(farmeragronomicDataItem.has("FarmExp")){
                input_farmingexperience.setText(farmeragronomicDataItem.getString("FarmExp"));
            }

            if(farmeragronomicDataItem.has("CropInsurance")){
                RadioGroup rg_type = rootView.findViewById(R.id.rg_cropinsurance);
                if (farmeragronomicDataItem.getBoolean("CropInsurance"))
                    rg_type.check(R.id.rb_cropinsuranceyes);
                else
                    rg_type.check(R.id.rb_cropinsurancegno);
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void refreshCropHistoryListView(){
        try{
            if(farmeragronomicDataItem.has("CropHistory")){
                tl_crophistory.removeAllViews();
                JSONArray cropHistory = farmeragronomicDataItem.getJSONArray("CropHistory");
                if(cropHistory.length() > 0){
                    TableRow row = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.tablerow_crophistory   , null);
                    ((TextView)row.findViewById(R.id.tv_id)).setText("Id");
                    ((TextView)row.findViewById(R.id.tv_year)).setText("Year");
                    ((TextView)row.findViewById(R.id.tv_month)).setText("Month");
                    ((TextView)row.findViewById(R.id.tv_acers)).setText("Acers");
                    ((TextView)row.findViewById(R.id.tv_crop)).setText("Crop");
                    ((TextView)row.findViewById(R.id.tv_production)).setText("Production");
                    ((TextView)row.findViewById(R.id.tv_rate)).setText("Rate");
                    ((ImageView)row.findViewById(R.id.ch_delete)).setVisibility(View.GONE);
                    ((ImageView)row.findViewById(R.id.ch_edit)).setVisibility(View.GONE);
                    tl_crophistory.addView(row);
                }
                for(int i = 0; i < cropHistory.length(); i++){
                    TableRow row = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.tablerow_crophistory   , null);
                    ((TextView)row.findViewById(R.id.tv_id)).setText(String.valueOf(cropHistory.getJSONObject(i).getInt("Id")));
                    ((TextView)row.findViewById(R.id.tv_year)).setText(cropHistory.getJSONObject(i).getString("Year"));
                    ((TextView)row.findViewById(R.id.tv_month)).setText(cropHistory.getJSONObject(i).getString("Month"));
                    ((TextView)row.findViewById(R.id.tv_acers)).setText(cropHistory.getJSONObject(i).getString("Acers"));
                    ((TextView)row.findViewById(R.id.tv_crop)).setText(cropHistory.getJSONObject(i).getString("Crop"));
                    ((TextView)row.findViewById(R.id.tv_production)).setText(cropHistory.getJSONObject(i).getString("Production"));
                    ((TextView)row.findViewById(R.id.tv_rate)).setText(cropHistory.getJSONObject(i).getString("Rate"));

                    ImageView ch_delete = row.findViewById(R.id.ch_delete);
                    ch_delete.setOnClickListener(this);

                    ImageView ch_edit = row.findViewById(R.id.ch_edit);
                    ch_edit.setOnClickListener(this);
                    row.setId(i);
                    tl_crophistory.addView(row);
                }
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void modifyTypeOfCropArray(String cropType, boolean checked){
        try{
            JSONArray cropTypeArray = null;
            if(farmeragronomicDataItem.has("CropType"))
                cropTypeArray = farmeragronomicDataItem.getJSONArray("CropType");
            else
                cropTypeArray = new JSONArray();
            if(checked)
                cropTypeArray.put(cropType);
            else{
                JSONArray newArray = new JSONArray();
                for(int i = 0; i < cropTypeArray.length(); i++){
                    if(!cropTypeArray.getString(i).equalsIgnoreCase(cropType))
                        newArray.put(cropTypeArray.getString(i));
                }
                cropTypeArray = newArray;
            }
            farmeragronomicDataItem.put("CropType", cropTypeArray);

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
            if(v instanceof RadioButton)
                checked = ((RadioButton) v).isChecked();


            switch (v.getId()){
                case R.id.rb_ftsmall:
                    farmeragronomicDataItem.put("FarmerType", "Small");
                    break;
                case R.id.rb_ftmarginal:
                    farmeragronomicDataItem.put("FarmerType", "Marginal");
                    break;
                case R.id.rb_ftother:
                    farmeragronomicDataItem.put("FarmerType", "Other");
                    break;
                case R.id.rb_fcowner:
                    farmeragronomicDataItem.put("FarmerCategory", "Owner");
                    break;
                case R.id.rb_fctenant:
                    farmeragronomicDataItem.put("FarmerCategory", "Tenant");
                    break;
                case R.id.rb_fcsharecropper:
                    farmeragronomicDataItem.put("FarmerCategory", "SharedCropper");
                    break;
                case R.id.chk_toccotton:
                    modifyTypeOfCropArray("Cotton", checked);
                    break;
                case R.id.chk_tocchilli:
                    modifyTypeOfCropArray("Chilli", checked);
                    break;
                case R.id.chk_toccorn:
                    modifyTypeOfCropArray("Corn", checked);
                    break;
                case R.id.chk_tocother:
                    modifyTypeOfCropArray("Other", checked);
                    if(!checked)
                        farmeragronomicDataItem.remove("CropTypeOther");
                    if(checked)
                        (rootView.findViewById(R.id.input_layout_tocother)).setVisibility(View.VISIBLE);
                    else
                        (rootView.findViewById(R.id.input_layout_tocother)).setVisibility(View.GONE);
                    break;
                case R.id.rb_soilregadi:
                    farmeragronomicDataItem.put("SoilType", "Regadi");
                    farmeragronomicDataItem.remove("SoilTypeOther");
                    (rootView.findViewById(R.id.input_layout_soilother)).setVisibility(View.GONE);
                    break;
                case R.id.rb_soilnallaregadi:
                    farmeragronomicDataItem.put("SoilType", "NallaRegadi");
                    farmeragronomicDataItem.remove("SoilTypeOther");
                    (rootView.findViewById(R.id.input_layout_soilother)).setVisibility(View.GONE);
                    break;
                case R.id.rb_soilother:
                    farmeragronomicDataItem.put("SoilType", "Other");
                    if(checked)
                        (rootView.findViewById(R.id.input_layout_soilother)).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_soiltestingyes:
                    farmeragronomicDataItem.put("SoilTesting", true);
                    break;
                case R.id.rb_soiltestingno:
                    farmeragronomicDataItem.put("SoilTesting", false);
                    break;
                case R.id.rb_cropinsuranceyes:
                    farmeragronomicDataItem.put("CropInsurance", true);
                    break;
                case R.id.rb_cropinsurancegno:
                    farmeragronomicDataItem.put("CropInsurance", false);
                    break;
                case R.id.rb_crophistoryyes:
                    rootView.findViewById(R.id.ll_fabcrophistorycontainer).setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_crophistoryno:
                    rootView.findViewById(R.id.ll_fabcrophistorycontainer).setVisibility(View.GONE);
                    break;
                case R.id.fab_addcrophistory:
                    //new DialogAddCropHistoryItem(getActivity(), null, this).show();
                    if(validateForm()) {
                        saveForm();
                        globalVariables.setagronomicDataItemId(currentItemId);
                        if (isMapsServicesOK())
                            getLocationPermission();
                    }
                    break;
                case R.id.btn_agronomicslavedatasave:
                    if(validateForm()) {
                        saveForm();
                        goBack();
                    }
                    break;
                case R.id.ch_delete:
                    deleteCropHistoryItem(v);
                    break;
                case R.id.ch_edit:
                    editCropHistoryItem(v);
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public boolean isMapsServicesOK(){
        try{
            int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());
            if(available == ConnectionResult.SUCCESS)
                return true;
            else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
                dialog.show();
                return true;
            }else {
                Toast.makeText(getActivity(), "Your device do not support maps request", Toast.LENGTH_LONG).show();
                return false;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return false;
    }

    private void getLocationPermission(){
        try{
            if(Build.VERSION.SDK_INT < 23){
                loadAgronomicMapFiledFragment();
            }else {
                String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        loadAgronomicMapFiledFragment();
                    } else {
                        requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
                }
            }
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }
        catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            switch (requestCode) {
                case LOCATION_PERMISSION_REQUEST_CODE: {
                    if (grantResults.length > 0) {
                        for (int i = 0; i < grantResults.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(getActivity(), "Can not map farm with out location access permission", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        loadAgronomicMapFiledFragment();
                    }
                }
            }
        }catch (SecurityException secEx){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), secEx.getMessage().toString());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void loadAgronomicMapFiledFragment(){
        try{
            Bundle args = new Bundle();
            args.putInt("ItemId", currentItemId);
            Fragment fragment = new FragmentAFAgronomicMapField();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slideinleft,R.anim.slideoutleft);
            fragmentTransaction.replace(R.id.frame, fragment, "AgronomicMapField");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void editCropHistoryItem(View view){
        try{
            int position = ((View)view.getParent()).getId();
            JSONObject item = farmeragronomicDataItem.getJSONArray("CropHistory").getJSONObject(position);
            ModelCropHistoryInformation cropHistoryInformation = new ModelCropHistoryInformation(item.getInt("Id")
                    , item.getString("Year")
                    , item.getString("Month")
                    , item.getString("Acers")
                    , item.getString("Crop")
                    , item.getString("Production")
                    , item.getString("Rate"));
            new DialogAddCropHistoryItem(getActivity(), cropHistoryInformation, this).show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void deleteCropHistoryItem(View view){
        try{
            final int position = ((View)view.getParent()).getId();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                JSONArray oldlist = farmeragronomicDataItem.getJSONArray("CropHistory");
                                JSONArray newlist = new MiscMethods(getActivity()).deleteItem(oldlist, position);
                                farmeragronomicDataItem.put("CropHistory", newlist);
                                refreshCropHistoryListView();
                                dialog.cancel();
                            }catch (Exception ex){
                                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
                            }
                        }
                    });

            builder.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public boolean validateForm(){
        try{
            if(!farmeragronomicDataItem.has("FarmerType")){
                Toast.makeText(getActivity(), "Select farmer type", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!farmeragronomicDataItem.has("FarmerCategory")){
                Toast.makeText(getActivity(), "Select farmer category", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!farmeragronomicDataItem.has("CropType")){
                Toast.makeText(getActivity(), "Select crop type", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(input_layout_tocother.getVisibility() == View.VISIBLE &&
                    (!farmeragronomicDataItem.has("CropTypeOther") || farmeragronomicDataItem.getString("CropTypeOther").equalsIgnoreCase(""))){
                input_layout_tocother.setError("Enter crop type");
                requestFocus(input_tocother);
                return false;
            }else {
                input_layout_tocother.setErrorEnabled(false);
            }
            if(!farmeragronomicDataItem.has("SoilType")){
                Toast.makeText(getActivity(), "Select soil type", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(input_layout_soilother.getVisibility() == View.VISIBLE &&
                    (!farmeragronomicDataItem.has("SoilTypeOther") || farmeragronomicDataItem.getString("SoilTypeOther").equalsIgnoreCase(""))){
                input_layout_soilother.setError("Enter soil type");
                requestFocus(input_soilother);
                return false;
            }else {
                input_layout_soilother.setErrorEnabled(false);
            }
            if(spnr_watersource.getSelectedItem().toString().trim().equalsIgnoreCase("Select")){
                ((TextView)spnr_watersource.getSelectedView()).setError("Select water source");
                return false;
            }else{
                farmeragronomicDataItem.put("WaterSource", spnr_watersource.getSelectedItem().toString().trim());
            }
            if(!farmeragronomicDataItem.has("LandAcers") ||
                    (farmeragronomicDataItem.has("LandAcers") && farmeragronomicDataItem.getString("LandAcers").equalsIgnoreCase(""))){
                input_layout_landacers.setError("Enter land area");
                requestFocus(input_landacers);
                return false;
            }else {
                input_layout_landacers.setErrorEnabled(false);
            }
            if(!farmeragronomicDataItem.has("SoilTesting")){
                Toast.makeText(getActivity(), "Select soil testing status", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!farmeragronomicDataItem.has("FarmExp") ||
                    (farmeragronomicDataItem.has("FarmExp") && farmeragronomicDataItem.getString("FarmExp").equalsIgnoreCase(""))){
                input_layout_farmingexperience.setError("Enter farming experience");
                requestFocus(input_farmingexperience);
                return false;
            }else {
                input_layout_farmingexperience.setErrorEnabled(false);
            }
            if(!farmeragronomicDataItem.has("CropInsurance")){
                Toast.makeText(getActivity(), "Select crop insurance status", Toast.LENGTH_SHORT).show();
                return false;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return true;
    }

    public void saveForm(){
        try{
            if (currentItemId == -1) {
                currentItemId = farmeragronomicData.length();
                farmeragronomicDataItem.put("Id", currentItemId);
                farmeragronomicData.put(farmeragronomicDataItem);
            }
            else {
                if(farmeragronomicDataItem.has("FarmerType"))
                    farmeragronomicData.getJSONObject(currentItemId).put("FarmerType", farmeragronomicDataItem.get("FarmerType"));
                if(farmeragronomicDataItem.has("FarmerCategory"))
                    farmeragronomicData.getJSONObject(currentItemId).put("FarmerCategory", farmeragronomicDataItem.get("FarmerCategory"));
                if(farmeragronomicDataItem.has("CropType"))
                    farmeragronomicData.getJSONObject(currentItemId).put("CropType", farmeragronomicDataItem.get("CropType"));
                if(farmeragronomicDataItem.has("SoilType"))
                    farmeragronomicData.getJSONObject(currentItemId).put("SoilType", farmeragronomicDataItem.get("SoilType"));
                if(farmeragronomicDataItem.has("WaterSource"))
                    farmeragronomicData.getJSONObject(currentItemId).put("WaterSource", farmeragronomicDataItem.get("WaterSource"));
                if(farmeragronomicDataItem.has("LandAcers"))
                    farmeragronomicData.getJSONObject(currentItemId).put("LandAcers", farmeragronomicDataItem.get("LandAcers"));
                if(farmeragronomicDataItem.has("SoilTypeOther"))
                    farmeragronomicData.getJSONObject(currentItemId).put("SoilTypeOther", farmeragronomicDataItem.get("SoilTypeOther"));
                if(farmeragronomicDataItem.has("CropTypeOther"))
                    farmeragronomicData.getJSONObject(currentItemId).put("CropTypeOther", farmeragronomicDataItem.get("CropTypeOther"));
                if(farmeragronomicDataItem.has("FarmExp"))
                    farmeragronomicData.getJSONObject(currentItemId).put("FarmExp", farmeragronomicDataItem.get("FarmExp"));
                if(farmeragronomicDataItem.has("SoilTesting"))
                    farmeragronomicData.getJSONObject(currentItemId).put("SoilTesting", farmeragronomicDataItem.get("SoilTesting"));
                if(farmeragronomicDataItem.has("CropInsurance"))
                    farmeragronomicData.getJSONObject(currentItemId).put("CropInsurance", farmeragronomicDataItem.get("CropInsurance"));
                if(farmeragronomicDataItem.has("FarmMap"))
                    farmeragronomicData.getJSONObject(currentItemId).put("FarmMap", farmeragronomicDataItem.get("FarmMap"));
            }
            dbHelper.setParameter(getString(R.string.Agronomic), farmeragronomicData.toString());
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

    private void requestFocus(View view) {
        if (view.requestFocus())
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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
                    case R.id.input_landacers:
                        farmeragronomicDataItem.put("LandAcers", input_landacers.getText().toString().trim());
                        break;
                    case R.id.input_farmingexperience:
                        farmeragronomicDataItem.put("FarmExp", input_farmingexperience.getText().toString().trim());
                        break;
                    case R.id.input_soilother:
                        if(!input_soilother.getText().toString().trim().equalsIgnoreCase(""))
                        farmeragronomicDataItem.put("SoilTypeOther", input_soilother.getText().toString().trim());
                        break;
                    case R.id.input_tocother:
                        if(!input_tocother.getText().toString().trim().equalsIgnoreCase(""))
                            farmeragronomicDataItem.put("CropTypeOther", input_tocother.getText().toString().trim());
                        break;
                }
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
            }
        }
    }
}