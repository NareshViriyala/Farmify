package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.WebServiceOperation;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelDatabaseImage;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelSystemParameter;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddFarmer extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private ListView lv_addFarmData;
    private View rootView = null;
    private AdapterFarmerData adapterFarmerData;
    private String CURRENT_TAG = "";
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private Button btn_farmerdiscard, btn_farmersave;
    private WebServiceOperation wso;

    public FragmentAddFarmer(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_addfarmer, container, false);
        try {
            lv_addFarmData = (ListView) rootView.findViewById(R.id.lv_addFarmData);
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Add Farmer");
            dbHelper = new DatabaseHelper(getActivity());
            //dbHelper.deleteImage(15);
            btn_farmersave = rootView.findViewById(R.id.btn_farmersave);
            btn_farmerdiscard = rootView.findViewById(R.id.btn_farmerdiscard);
            wso = new WebServiceOperation();

            List<ModelSystemParameter> list = dbHelper.getAllParameters();
            List<ModelDatabaseImage> imageList = dbHelper.getAllImages();
            Toast.makeText(getActivity(), "list - "+String.valueOf(list.size())+", images - "+String.valueOf(imageList.size()), Toast.LENGTH_SHORT).show();

            btn_farmerdiscard.setOnClickListener(this);
            btn_farmersave.setOnClickListener(this);
            populateListView();

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
        return rootView;
    }

    public void populateListView(){
        try{
            ArrayList<ModelFarmerData> farmerDataList = new ArrayList<>();
            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_person_black_24dp
                    , "Individual"
                    , "Farmer's basic individual data will be collected"
                    , dbHelper.getParameter(getString(R.string.IndividualStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.IndividualStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_bank_black_24dp
                    , "Bank"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.BankStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.BankStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_social_black_24dp
                    , "Social"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.SocialStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.SocialStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_agronomic_black_24dp
                    , "Agronomic"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.AgronomicStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.AgronomicStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_commerce_black_24dp
                    , "Commerce"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.CommerceStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.CommerceStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_dealer_black_24dp
                    , "Partners"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.PartnerStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.PartnerStatus)))));

            farmerDataList.add(new ModelFarmerData(R.drawable.baseline_add_photo_alternate_black_24dp
                    , "Images"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.ImagesStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.ImagesStatus)))));

            adapterFarmerData = new AdapterFarmerData(this.getContext(), farmerDataList);
            lv_addFarmData.setAdapter(adapterFarmerData);
            lv_addFarmData.setOnItemClickListener(this);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView datatype = (TextView) view.findViewById(R.id.textView_datatype);
        final String datatype_name = datatype.getText().toString();


        Fragment fragment = getFragment(datatype_name);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slideinleft,R.anim.slideoutleft);
        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();

       /* Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getFragment(datatype_name);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };*/
    }

    public Fragment getFragment(String fragmentname){
        try{
            switch (fragmentname) {
                case "Individual":
                    FragmentScanQR fragmentScanQR = new FragmentScanQR();
                    return fragmentScanQR;
                    /*FragmentAFIndividual fragmentIndividual = new FragmentAFIndividual();
                    return fragmentIndividual;*/
                case "Bank":
                    FragmentAFBank fragmentBank = new FragmentAFBank();
                    return fragmentBank;
                case "Social":
                    FragmentAFSocial fragmentSocial = new FragmentAFSocial();
                    return fragmentSocial;
                case "Agronomic":
                    FragmentAFAgronomic fragmentAgronomics = new FragmentAFAgronomic();
                    return fragmentAgronomics;
                case "Commerce":
                    FragmentAFCommerce fragmentCommerce = new FragmentAFCommerce();
                    return fragmentCommerce;
                case "Partners":
                    FragmentAFPartner fragmentPartner = new FragmentAFPartner();
                    return fragmentPartner;
                case "Images":
                    FragmentAFImages fragmentImages = new FragmentAFImages();
                    return fragmentImages;
                default:
                    return new FragmentHome();
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return new FragmentHome();
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_farmersave:
                    if(!saveFarmerData(getResources().getString(R.string.IndividualStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.BankStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.SocialStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.AgronomicStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.CommerceStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.PartnerStatus)))
                        return;
                    if(!saveFarmerData(getResources().getString(R.string.ImagesStatus)))
                        return;

                    JSONObject payload = new JSONObject();
                    payload.put("agent_id", dbHelper.getParameter("user_id"));
                    payload.put("individual_data", new JSONObject(dbHelper.getParameter(getResources().getString(R.string.Individual))));
                    payload.put("bank_data", new JSONObject(dbHelper.getParameter(getResources().getString(R.string.Bank))));
                    payload.put("social_data", new JSONObject(dbHelper.getParameter(getResources().getString(R.string.Social))));
                    payload.put("agronomic_data", new JSONArray(dbHelper.getParameter(getResources().getString(R.string.Agronomic))));
                    payload.put("commerce_data", new JSONObject(dbHelper.getParameter(getResources().getString(R.string.Commerce))));
                    payload.put("partner_data", new JSONArray(dbHelper.getParameter(getResources().getString(R.string.Partner))));
                    payload.put("image_data", prepareImageJson());

                    new sendPayLoad().execute(payload.toString(), dbHelper.getParameter("token"));
                    break;
                case R.id.btn_farmerdiscard:
                    deleteFarmerData();
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public JSONObject prepareImageJson(){
        JSONObject ImagesActual = new JSONObject();
        try{
            JSONObject imagesReference = new JSONObject(dbHelper.getParameter(getResources().getString(R.string.Images)));
            if(imagesReference.has("Farmer")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Farmer");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Farmer", ImagesString);
            }

            if(imagesReference.has("Aadharcard")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Aadharcard");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Aadharcard", ImagesString);
            }

            if(imagesReference.has("Bankbook")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Bankbook");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Bankbook", ImagesString);
            }

            if(imagesReference.has("Rationcard")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Rationcard");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Rationcard", ImagesString);
            }

            if(imagesReference.has("Pancard")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Pancard");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Pancard", ImagesString);
            }

            if(imagesReference.has("Additional")){
                JSONArray ImagesInt = imagesReference.getJSONArray("Additional");
                JSONArray ImagesString = new JSONArray();
                for(int i = 0; i < ImagesInt.length(); i++){
                    byte[] image = dbHelper.getImage(ImagesInt.getInt(i));
                    ImagesString.put(Base64.encodeToString(image, Base64.DEFAULT));
                }
                ImagesActual.put("Additional", ImagesString);
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return ImagesActual;
    }

    public class sendPayLoad extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected void onPreExecute(){
            (rootView.findViewById(R.id.pb_loadingsavefarmer)).setVisibility(View.VISIBLE);
            btn_farmersave.setText("");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject response = new JSONObject();
            try {
                response = wso.MakePostCall("FarmerData/logFarmerDetails", strings[0], strings[1]);
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                (rootView.findViewById(R.id.pb_loadingsavefarmer)).setVisibility(View.GONE);
                btn_farmersave.setText("Save");
                JSONObject response = new JSONObject(result.getString("response"));
                if (result.getInt("responseCode") == 200 && response.getInt("output") > 0) {
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Successfully saved to server", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    eraseFarmerData();
                }else{
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Saving locally", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    saveLocally();
                    eraseFarmerData();
                }
            } catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }

    }

    public void saveLocally(){
        try{

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public boolean saveFarmerData(String type){
        String responsetext = "";
        boolean response = false;
        try{
            String status = dbHelper.getParameter(type);

            if(status.equalsIgnoreCase("0") || status.equalsIgnoreCase(""))
                responsetext = "Save "+type.replace("_Status","")+" details";
            else if(status.equalsIgnoreCase("1"))
                responsetext = "Complete "+type.replace("_Status","")+" details";
            else
                response = true;

            if(!response)
                Snackbar.make(getActivity().findViewById(R.id.fab), responsetext, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return response;
    }

    public void eraseFarmerData(){
        try{
            dbHelper.deleteParameter(getResources().getString(R.string.Individual));
            dbHelper.deleteParameter(getResources().getString(R.string.IndividualStatus));
            dbHelper.deleteParameter(getResources().getString(R.string.Bank));
            dbHelper.deleteParameter(getResources().getString(R.string.BankStatus));
            dbHelper.deleteParameter(getResources().getString(R.string.Social));
            dbHelper.deleteParameter(getResources().getString(R.string.SocialStatus));
            dbHelper.deleteParameter(getResources().getString(R.string.Agronomic));
            dbHelper.deleteParameter(getResources().getString(R.string.AgronomicStatus));
            dbHelper.deleteParameter(getResources().getString(R.string.Commerce));
            dbHelper.deleteParameter(getResources().getString(R.string.CommerceStatus));
            dbHelper.deleteParameter(getResources().getString(R.string.Partner));
            dbHelper.deleteParameter(getResources().getString(R.string.PartnerStatus));
            String data = dbHelper.getParameter(getResources().getString(R.string.Images));
            JSONObject jsonObject = null;
            if(data.isEmpty()){
                dbHelper.deleteParameter(getResources().getString(R.string.Images));
                dbHelper.deleteParameter(getResources().getString(R.string.ImagesStatus));
            }
            else {
                jsonObject = new JSONObject(data);
                String[] image_types = getResources().getStringArray(R.array.image_types);
                for (String item:image_types) {
                    if(jsonObject.has(item.replace(" ", ""))){
                        JSONArray jsonArray = jsonObject.getJSONArray(item.replace(" ",""));
                        for (int i = 0; i < jsonArray.length(); i++){
                            dbHelper.deleteImage(jsonArray.getInt(i));
                        }
                    }
                }
                dbHelper.deleteParameter(getResources().getString(R.string.Images));
                dbHelper.deleteParameter(getResources().getString(R.string.ImagesStatus));
            }
            populateListView();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void deleteFarmerData(){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Delete entered data, please confirm?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                eraseFarmerData();
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

    /*@Override
    public void onResume(){
        super.onResume();
        try {
            getFragmentManager().popBackStackImmediate(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }*/
}