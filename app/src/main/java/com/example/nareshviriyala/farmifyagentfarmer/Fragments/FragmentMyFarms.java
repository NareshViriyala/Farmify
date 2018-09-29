package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterFarmInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.WebServiceOperation;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelAgronomicInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentMyFarms extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private View rootView;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private ListView lv_farmslist;
    private AdapterFarmInformation adapterFarmInformation;
    private ImageView img_search;
    public Validations validations;
    private EditText input_phone;
    private TextInputLayout input_layout_phone;
    private ProgressBar pb_loading;
    private WebServiceOperation wso;
    private JSONArray farmList;

    public FragmentMyFarms(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("phone", input_phone.getText().toString().trim());
        outState.putString("FarmData", farmList.toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_myfarms, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Farm Details");
            dbHelper = new DatabaseHelper(getActivity());
            validations = new Validations();
            wso = new WebServiceOperation();

            pb_loading = rootView.findViewById(R.id.pb_loading);

            lv_farmslist = rootView.findViewById(R.id.lv_farmslist);
            lv_farmslist.setOnItemClickListener(this);

            input_phone = rootView.findViewById(R.id.input_phone);
            input_layout_phone = rootView.findViewById(R.id.input_layout_phone);

            img_search = rootView.findViewById(R.id.img_search);
            img_search.setOnClickListener(this);

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            loadMyFarmsSlaveFragment(position);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void loadMyFarmsSlaveFragment(int ItemId){
        try{
            Bundle args = new Bundle();
            args.putInt("ItemId", ItemId);
            Fragment fragment = new FragmentMyFarmsSlave();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slideinleft,R.anim.slideoutleft);
            fragmentTransaction.replace(R.id.frame, fragment, "FragmentMyFarmsSlave");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.img_search:
                    if (!validatePhone())
                        return;
                    new getFarmDataFromServer().execute(input_phone.getText().toString().trim(), dbHelper.getParameter("token"));
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public class getFarmDataFromServer extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            img_search.setVisibility(View.GONE);
        }

        @Override
        protected JSONObject doInBackground(String[] params) {
            //android.os.Debug.waitForDebugger();
            JSONObject response = null;
            try {
                response = wso.MakeGetCall("FarmData/getFarmData?phone="+params[0], params[1]);

            }
            catch (Exception e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());
            }

            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result){
            super.onPostExecute(result);
            pb_loading.setVisibility(View.GONE);
            img_search.setVisibility(View.VISIBLE);
            try {

                if (result.getInt("responseCode") == 200) {
                    JSONObject response = new JSONObject(result.getString("response"));
                    farmList = response.getJSONArray("result");
                    refreshFarmList(farmList);
                } else if(result.getInt("responseCode") == 401){
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Session expired", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    JSONObject error = new JSONObject(result.getString("response"));
                    Snackbar.make(getActivity().findViewById(R.id.fab), error.getString("result"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        }
    }

    public void refreshFarmList(JSONArray farmeragronomicData){
        try{

            ArrayList<ModelAgronomicInformation> farmInformation = new ArrayList<>();
            for(int i = 0; i < farmeragronomicData.length(); i++){
                JSONObject item = farmeragronomicData.getJSONObject(i);
                farmInformation.add(new ModelAgronomicInformation(item.has("id")?item.getInt("id"):0
                        , item.has("farmer_type")?item.getString("farmer_type"):""
                        , item.has("farmer_category")?item.getString("farmer_category"):""
                        , item.has("crop_type")?item.getString("crop_type"):""
                        , item.has("soil_type")?item.getString("soil_type"):""
                        , item.has("water_source")?item.getString("water_source"):""
                        , item.has("land_acers")?item.getString("land_acers"):""
                        , item.has("soil_testing")?item.getBoolean("soil_testing"):false
                        , item.has("crop_insurance")?item.getBoolean("crop_insurance"):false
                        , item.has("farm_exp")?item.getString("farm_exp"):""));
            }
            if(farmInformation.size() > 0)
                lv_farmslist.setVisibility(View.VISIBLE);
            else
                lv_farmslist.setVisibility(View.GONE);

            adapterFarmInformation = new AdapterFarmInformation(this.getContext(), farmInformation, null);
            lv_farmslist.setAdapter(adapterFarmInformation);

            /*if(adapterFarmInformation == null) {
                adapterFarmInformation = new AdapterFarmInformation(this.getContext(), farmInformation, this);
                lv_farmlist.setAdapter(adapterFarmInformation);
            }else
                adapterFarmInformation.updateFarmList(farmInformation);*/
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
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

    private void requestFocus(View view) {
        if (view.requestFocus())
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}