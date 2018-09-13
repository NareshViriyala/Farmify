package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterPartnerInformation;
import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddPartnerItem;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelPartnerInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentAFPartner extends Fragment implements View.OnClickListener{

    private View rootView;
    private LogErrors logErrors;
    private DatabaseHelper dbHelper;
    private String className;
    public JSONObject farmerpartnerData;
    private ListView lv_partnerlist;
    private Button btn_partnerdatasave;
    private FloatingActionButton fab_addpartner;
    private AdapterPartnerInformation adapterPartnerInformation;

    public FragmentAFPartner(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_af_partner, container, false);

        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            dbHelper = new DatabaseHelper(getActivity());
            String data = dbHelper.getParameter(getString(R.string.Partner));
            if(data.isEmpty() || data == null)
                farmerpartnerData = new JSONObject();
            else
                farmerpartnerData = new JSONObject(data);
            ((HomeActivity) getActivity()).setActionBarTitle("Partner Details");

            btn_partnerdatasave = rootView.findViewById(R.id.btn_partnerdatasave);
            btn_partnerdatasave.setOnClickListener(this);

            fab_addpartner = rootView.findViewById(R.id.fab_addpartner);
            fab_addpartner.setOnClickListener(this);

            lv_partnerlist = rootView.findViewById(R.id.lv_partnerlist);

            refreshPartnerListView();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void refreshPartnerListView(){
        try{
            if(!farmerpartnerData.has(getResources().getString(R.string.Partner))){
                lv_partnerlist.setVisibility(View.GONE);
                return;
            }
            ArrayList<ModelPartnerInformation> partnerInformation = new ArrayList<>();
            JSONArray partnerList = farmerpartnerData.getJSONArray(getResources().getString(R.string.Partner));
            for(int i = 0; i < partnerList.length(); i++){
                JSONObject item = partnerList.getJSONObject(i);
                partnerInformation.add(new ModelPartnerInformation(item.getInt("Id"), item.getString("PartnerName"), item.getString("PartnerPhone"), item.getString("PartnerType")));
            }
            if(partnerInformation.size() > 0)
                lv_partnerlist.setVisibility(View.VISIBLE);
            else
                lv_partnerlist.setVisibility(View.GONE);

            if(adapterPartnerInformation == null) {
                adapterPartnerInformation = new AdapterPartnerInformation(this.getContext(), partnerInformation, this);
                lv_partnerlist.setAdapter(adapterPartnerInformation);
            }else
                adapterPartnerInformation.updatePartnerList(partnerInformation);

            dbHelper.setParameter(getString(R.string.Partner),farmerpartnerData.toString());
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
            switch (v.getId()) {
                case R.id.fab_addpartner:
                    new DialogAddPartnerItem(getActivity(), null, this).show();
                    break;
                case R.id.btn_partnerdatasave:
                    validatePartnerData();
                    break;
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void validatePartnerData()
    {
        try{
            dbHelper.setParameter(getString(R.string.Partner), farmerpartnerData.toString());
            dbHelper.setParameter(getString(R.string.PartnerStatus), "3");
            JSONArray list = null;
            if(farmerpartnerData.has(getResources().getString(R.string.Partner)))
                list = farmerpartnerData.getJSONArray(getResources().getString(R.string.Partner));
            if(list.length() <= 0 || list == null)
                dbHelper.setParameter(getString(R.string.PartnerStatus), "2");

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