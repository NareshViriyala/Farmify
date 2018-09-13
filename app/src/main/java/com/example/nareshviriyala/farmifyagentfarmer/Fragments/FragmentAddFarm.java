package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Adapters.AdapterFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.FarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import java.util.ArrayList;

public class FragmentAddFarm extends Fragment implements AdapterView.OnItemClickListener{
    private ListView lv_addFarmData;
    private View rootView = null;
    private AdapterFarmerData adapterFarmerData;
    private String CURRENT_TAG = "";
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;

    public FragmentAddFarm(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_addfarm, container, false);
        try {
            lv_addFarmData = (ListView) rootView.findViewById(R.id.lv_addFarmData);
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Add Farm");
            dbHelper = new DatabaseHelper(getActivity());
            populateListView();

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
        return rootView;
    }

    public void populateListView(){
        try{
            ArrayList<FarmerData> farmerDataList = new ArrayList<>();
            farmerDataList.add(new FarmerData(R.drawable.baseline_person_black_24dp
                    , "Individual"
                    , "Farmer's basic individual data will be collected"
                    , dbHelper.getParameter(getString(R.string.IndividualStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.IndividualStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_bank_black_24dp
                    , "Bank"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.BankStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.BankStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_social_black_24dp
                    , "Social"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.SocialStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.SocialStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_agronomic_black_24dp
                    , "Agronomic"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.AgronomicStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.AgronomicStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_commerce_black_24dp
                    , "Commerce"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.CommerceStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.CommerceStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_dealer_black_24dp
                    , "Partners"
                    , "Description to be provided later"
                    , dbHelper.getParameter(getString(R.string.PartnerStatus)).equalsIgnoreCase("")?0:Integer.parseInt(dbHelper.getParameter(getString(R.string.PartnerStatus)))));

            farmerDataList.add(new FarmerData(R.drawable.baseline_add_photo_alternate_black_24dp
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
                    /*FragmentScanQR fragmentScanQR = new FragmentScanQR();
                    return fragmentScanQR;*/
                    /*FragmentAFIndividual fragmentIndividual = new FragmentAFIndividual();
                    return fragmentIndividual;*/
                    FragmentCameraPicture fragmentCameraPicture = new FragmentCameraPicture();
                    return fragmentCameraPicture;
                case "Bank":
                    FragmentAFBank fragmentBank = new FragmentAFBank();
                    return fragmentBank;
                case "Social":
                    FragmentAFSocial fragmentSocial = new FragmentAFSocial();
                    return fragmentSocial;
                case "Agronomic":
                    FragmentAgronomics fragmentAgronomics = new FragmentAgronomics();
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

    /*@Override
    public void onResume(){
        super.onResume();
        try {
            getFragmentManager().popBackStackImmediate(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
        }
        catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
    }*/
}