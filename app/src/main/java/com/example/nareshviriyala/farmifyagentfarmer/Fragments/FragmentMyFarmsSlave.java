package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;

public class FragmentMyFarmsSlave extends Fragment implements View.OnClickListener{
    private View rootView;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private Spinner spnr_croptype;
    private EditText input_profitshare;
    private TextInputLayout input_layout_profitshare;
    private TableLayout tl_cropexpense;
    private ProgressBar pb_loading;
    private Button btn_save;

    public FragmentMyFarmsSlave(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_myfarmsslave, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Expense Details");
            dbHelper = new DatabaseHelper(getActivity());
            btn_save = rootView.findViewById(R.id.btn_save);
            btn_save.setOnClickListener(this);

            input_profitshare = rootView.findViewById(R.id.tl_cropexpense);
            input_layout_profitshare = rootView.findViewById(R.id.input_layout_profitshare);

            spnr_croptype = rootView.findViewById(R.id.spnr_croptype);

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_save:
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }
}