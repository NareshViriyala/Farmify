package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import java.util.Calendar;

public class FragmentIndividual extends Fragment implements View.OnClickListener{
    private View rootView;
    private LogErrors logErrors;
    private String className;
    private Button btn_verifyPhone, btn_sendotp, btn_save;
    private LinearLayout ll_verifyotp;
    private EditText input_age;
    private ImageView img_calendar;
    private int day, month, year;

    public FragmentIndividual(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_individual, container, false);
        try{
            logErrors = LogErrors.getInstance(getActivity());
            className = new Object(){}.getClass().getEnclosingClass().getName();
            ((HomeActivity) getActivity()).setActionBarTitle("Individual Details");
            btn_verifyPhone = (Button)rootView.findViewById(R.id.btn_verifyPhone);
            btn_sendotp = (Button)rootView.findViewById(R.id.btn_sendotp);
            btn_save = (Button)rootView.findViewById(R.id.btn_save);
            input_age = (EditText)rootView.findViewById(R.id.input_age);
            ll_verifyotp = (LinearLayout) rootView.findViewById(R.id.ll_verifyotp);
            img_calendar = (ImageView)rootView.findViewById(R.id.img_calendar);


            btn_verifyPhone.setOnClickListener(this);
            img_calendar.setOnClickListener(this);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    public void openCalendar(){
        try{
            Calendar mcurrentDate = Calendar.getInstance();
            year = mcurrentDate.get(Calendar.YEAR);
            month = mcurrentDate.get(Calendar.MONTH);
            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog  mDatePicker =new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
            {
                @Override
                public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
                {
                    //ed_date.setText(new StringBuilder().append(year).append("-").append(month+1).append("-").append(day));
                    day = selectedday;
                    month = selectedmonth + 1;
                    year = selectedyear;
                    String sday = String.valueOf(day);
                    sday = (sday.length() == 1)?("0"+sday):sday;
                    String smonth = String.valueOf(month);
                    smonth = (smonth.length() == 1)?("0"+smonth):smonth;
                    input_age.setText(sday + "/" + smonth+ "/" + String.valueOf(year));

                }
            },year, month, day);
            mDatePicker.setTitle("Please select date");
            // TODO Hide Future Date Here
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            // TODO Hide Past Date Here
            //  mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            mDatePicker.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.img_calendar:
                openCalendar();
                break;
            case R.id.btn_verifyPhone:
                ll_verifyotp.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
