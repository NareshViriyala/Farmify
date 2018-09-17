package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterFarmInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelAgronomicInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAFAgronomic extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private View rootView;
    private LogErrors logErrors;
    private String className;
    private FloatingActionButton fab_addfarm;
    private DatabaseHelper dbHelper;
    private ListView lv_farmlist;
    private AdapterFarmInformation adapterFarmInformation;

    public FragmentAFAgronomic(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_af_agronomic, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Agronomic Details");
            dbHelper = new DatabaseHelper(getActivity());
            lv_farmlist = rootView.findViewById(R.id.lv_farmlist);
            lv_farmlist.setOnItemClickListener(this);
            //dbHelper.deleteParameter(getResources().getString(R.string.Agronomic));

            fab_addfarm = rootView.findViewById(R.id.fab_addfarm);
            fab_addfarm.setOnClickListener(this);
            refreshFarmList();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void refreshFarmList(){
        try{
            String data = dbHelper.getParameter(getString(R.string.Agronomic));
            JSONArray farmeragronomicData = null;
            if(data.isEmpty() || data == null || data.equalsIgnoreCase("[]")) {
                farmeragronomicData = new JSONArray();
                dbHelper.setParameter(getResources().getString(R.string.AgronomicStatus), "0");
            }
            else {
                farmeragronomicData = new JSONArray(data);
                dbHelper.setParameter(getResources().getString(R.string.AgronomicStatus), "3");
            }

            ArrayList<ModelAgronomicInformation> farmInformation = new ArrayList<>();
            for(int i = 0; i < farmeragronomicData.length(); i++){
                JSONObject item = farmeragronomicData.getJSONObject(i);
                farmInformation.add(new ModelAgronomicInformation(item.has("Id")?item.getInt("Id"):0
                        , item.has("FarmerType")?item.getString("FarmerType"):""
                        , item.has("FarmerCategory")?item.getString("FarmerCategory"):""
                        , item.has("FarmerType")?item.getString("CropType"):""
                        , item.has("CropType")?item.getString("SoilType"):""
                        , item.has("WaterSource")?item.getString("WaterSource"):""
                        , item.has("LandAcers")?item.getString("LandAcers"):""
                        , item.has("Soiltesting")?item.getBoolean("Soiltesting"):false
                        , item.has("CropInsurance")?item.getBoolean("CropInsurance"):false
                        , item.has("FarmExp")?item.getString("FarmExp"):""));
            }
            if(farmInformation.size() > 0)
                lv_farmlist.setVisibility(View.VISIBLE);
            else
                lv_farmlist.setVisibility(View.GONE);

            adapterFarmInformation = new AdapterFarmInformation(this.getContext(), farmInformation, this);
            lv_farmlist.setAdapter(adapterFarmInformation);

            /*if(adapterFarmInformation == null) {
                adapterFarmInformation = new AdapterFarmInformation(this.getContext(), farmInformation, this);
                lv_farmlist.setAdapter(adapterFarmInformation);
            }else
                adapterFarmInformation.updateFarmList(farmInformation);*/
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.fab_addfarm:
                    loadAgronomicSlaveFragment(-1);
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void loadAgronomicSlaveFragment(int ItemId){
        try{
            Bundle args = new Bundle();
            args.putInt("ItemId", ItemId);
            Fragment fragment = new FragmentAFAgronomicSlave();
            fragment.setArguments(args);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slideinleft,R.anim.slideoutleft);
            fragmentTransaction.replace(R.id.frame, fragment, "AgronomicSlave");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            loadAgronomicSlaveFragment(position);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    /*@Override
    public void onResume(){
        super.onResume();
        refreshFarmList();
    }*/
}