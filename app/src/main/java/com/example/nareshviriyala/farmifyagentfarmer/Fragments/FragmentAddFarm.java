package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Models.FarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddFarm extends Fragment {
    private ListView lv_addFarmData;
    private View rootView = null;
    private AdapterFarmerData adapterFarmerData;

    public FragmentAddFarm(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_addfarm, container, false);
        lv_addFarmData = (ListView)rootView.findViewById(R.id.lv_addFarmData);
        ArrayList<FarmerData> farmerDataList = new ArrayList<>();
        farmerDataList.add(new FarmerData(R.drawable.baseline_person_black_24dp, "Individual", "Farmer's basic individual data will be collected", 0));
        farmerDataList.add(new FarmerData(R.drawable.baseline_bank_black_24dp, "Bank", "Description to be provided later", 0));
        farmerDataList.add(new FarmerData(R.drawable.baseline_social_black_24dp, "Social", "Description to be provided later", 1));
        farmerDataList.add(new FarmerData(R.drawable.baseline_agronomic_black_24dp, "Agronomic", "Description to be provided later", 0));
        farmerDataList.add(new FarmerData(R.drawable.baseline_commerce_black_24dp, "Commerce", "Description to be provided later", 2));
        farmerDataList.add(new FarmerData(R.drawable.baseline_dealer_black_24dp, "Dealer", "Description to be provided later", 3));
        farmerDataList.add(new FarmerData(R.drawable.baseline_pesticide_black_24dp, "Pesticide shop owner", "Description to be provided later", 0));
        adapterFarmerData = new AdapterFarmerData(this.getContext(), farmerDataList);
        lv_addFarmData.setAdapter(adapterFarmerData);
        return rootView;
    }
}