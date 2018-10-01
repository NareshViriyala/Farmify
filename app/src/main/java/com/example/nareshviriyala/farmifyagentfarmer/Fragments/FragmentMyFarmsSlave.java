package com.example.nareshviriyala.farmifyagentfarmer.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Activities.HomeActivity;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.WebServiceOperation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Calendar;

public class FragmentMyFarmsSlave extends Fragment implements View.OnClickListener{
    private View rootView;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;
    private Spinner spnr_croptype, spnr_profitshare;
    private TableLayout tl_cropexpense;
    private ProgressBar pb_loading;
    private Button btn_save;
    private FloatingActionButton fab_add_newrow;
    private int day, month, year;
    private TextInputLayout input_layout_landacers, input_layout_startdate, input_layout_enddate;
    private EditText input_landacers, input_startdate, input_enddate;
    private ImageView img_startcalendar, img_endcalendar;
    private int farm_id, farm_expense_id = 0;
    private WebServiceOperation wso;
    private JSONObject expenseData;
    private String expenseDataSHA1;

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
            wso = new WebServiceOperation();

            farm_id = getArguments().getInt("ItemId");

            btn_save = rootView.findViewById(R.id.btn_save);
            btn_save.setOnClickListener(this);

            pb_loading = rootView.findViewById(R.id.pb_loading);

            spnr_croptype = rootView.findViewById(R.id.spnr_croptype);
            spnr_profitshare = rootView.findViewById(R.id.spnr_profitshare);

            tl_cropexpense = rootView.findViewById(R.id.tl_cropexpense);

            input_layout_landacers = rootView.findViewById(R.id.input_layout_landacers);
            input_layout_startdate = rootView.findViewById(R.id.input_layout_startdate);
            input_layout_enddate = rootView.findViewById(R.id.input_layout_enddate);

            input_landacers = rootView.findViewById(R.id.input_landacers);
            input_startdate = rootView.findViewById(R.id.input_startdate);
            input_enddate = rootView.findViewById(R.id.input_enddate);

            fab_add_newrow = rootView.findViewById(R.id.fab_add_newrow);
            fab_add_newrow.setOnClickListener(this);

            img_startcalendar = rootView.findViewById(R.id.img_startcalendar);
            img_startcalendar.setOnClickListener(this);

            img_endcalendar = rootView.findViewById(R.id.img_endcalendar);
            img_endcalendar.setOnClickListener(this);
            new getFarmExpenseDetails().execute(String.valueOf(farm_id), dbHelper.getParameter("token"));

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return rootView;
    }

    private void fillView(JSONObject jsonObject){
        try{
            if(jsonObject == null || jsonObject.toString().equalsIgnoreCase("{}")) {
                fillTable(null);
                return;
            }

            if(jsonObject.has("farm_expense_id")){
                farm_expense_id = jsonObject.getInt("farm_expense_id");
            }

            if(jsonObject.has("crop_type")){
                String[] wsType = getActivity().getResources().getStringArray(R.array.crop_type);
                int index = Arrays.asList(wsType).indexOf(jsonObject.getString("crop_type"));
                spnr_croptype.setSelection(index, true);
            }

            if(jsonObject.has("land_acers")){
                input_landacers.setText(jsonObject.getString("land_acers"));
            }

            if(jsonObject.has("profit_share")){
                String[] wsType = getActivity().getResources().getStringArray(R.array.profit_share);
                int index = Arrays.asList(wsType).indexOf(jsonObject.getString("profit_share"));
                spnr_profitshare.setSelection(index, true);
            }

            if(jsonObject.has("start_date")){
                input_startdate.setText(jsonObject.getString("start_date"));
            }

            if(jsonObject.has("end_date")){
                input_enddate.setText(jsonObject.getString("end_date"));
            }

            if(jsonObject.has("expenses")){
                JSONArray jsonArray = jsonObject.getJSONArray("expenses");
                fillTable(jsonArray);
            }

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void fillTable(JSONArray jsonArray){
        try{
            createTableHeader();
            if(jsonArray == null || jsonArray.length() == 0)
                addNewRowToTable(null);
            else{
                for(int i = 0; i < jsonArray.length(); i++)
                    addNewRowToTable(jsonArray.getJSONObject(i));
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void createTableHeader(){
        try{
            TableRow tbrow0 = new TableRow(getActivity());

            TextView tv0 = new TextView(getActivity());
            tv0.setText(" Sl.No ");
            tv0.setTextColor(Color.BLACK);
            tv0.setVisibility(View.GONE);
            tbrow0.addView(tv0);

            TextView tvdummy = new TextView(getActivity());
            tvdummy.setText("#");
            tvdummy.setTextColor(Color.BLACK);
            tbrow0.addView(tvdummy);

            TextView tv1 = new TextView(getActivity());
            tv1.setText(" Expense ");
            tv1.setTextColor(Color.BLACK);
            tbrow0.addView(tv1);

            TextView tv2 = new TextView(getActivity());
            tv2.setText(" Amount ");
            tv2.setTextColor(Color.BLACK);
            tbrow0.addView(tv2);

            TextView tv3 = new TextView(getActivity());
            tv3.setText(" Date ");
            tv3.setTextColor(Color.BLACK);
            tbrow0.addView(tv3);

            tl_cropexpense.addView(tbrow0, 0);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private boolean validateTableRows(){
        try{
            int rowcount = tl_cropexpense.getChildCount();
            if(rowcount >= 1){ //why 1 ? because header row is treated as 1 row
                for(int i = 1; i < rowcount; i++){
                    TableRow row = (TableRow)tl_cropexpense.getChildAt(i);
                    EditText input_expensedesc = row.findViewById(R.id.input_expensedesc);
                    EditText input_expenseamount = row.findViewById(R.id.input_expenseamount);
                    EditText input_expensedate = row.findViewById(R.id.input_expensedate);

                    TextInputLayout input_layout_expensedesc = row.findViewById(R.id.input_layout_expensedesc);
                    TextInputLayout input_layout_expenseamount = row.findViewById(R.id.input_layout_expenseamount);
                    TextInputLayout input_layout_expensedate = row.findViewById(R.id.input_layout_expensedate);

                    if(input_expensedesc.getText().toString().trim().equalsIgnoreCase("")){
                        input_layout_expensedesc.setError("Enter decription");
                        requestFocus(input_expensedesc);
                        return false;
                    }else{
                        input_layout_expensedesc.setErrorEnabled(false);
                    }

                    if(input_expenseamount.getText().toString().trim().equalsIgnoreCase("")){
                        input_layout_expenseamount.setError("Enter amount");
                        requestFocus(input_expenseamount);
                        return false;
                    }else{
                        input_layout_expenseamount.setErrorEnabled(false);
                    }

                    if(input_expensedate.getText().toString().trim().equalsIgnoreCase("")){
                        input_layout_expensedate.setError("Enter date");
                        requestFocus(input_expensedate);
                        return false;
                    }else{
                        input_layout_expensedate.setErrorEnabled(false);
                    }
                }
            }else
                return true;

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return true;
    }

    private void addNewRowToTable(JSONObject jsonObject){
        try{
            TableRow row = (TableRow)LayoutInflater.from(getActivity()).inflate(R.layout.tablerow_farmexpense   , null);
            ImageView img_deleterow = row.findViewById(R.id.img_deleterow);
            img_deleterow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow row = (TableRow) v.getParent();
                    int row_id = tl_cropexpense.indexOfChild(row);
                    tl_cropexpense.removeViewAt(row_id);
                }
            });
            ImageView img_calendar = row.findViewById(R.id.img_calendar);
            img_calendar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow row = (TableRow) (v.getParent().getParent());
                    int row_id = tl_cropexpense.indexOfChild(row);;
                    openCalendar((EditText) tl_cropexpense.getChildAt(row_id).findViewById(R.id.input_expensedate));
                }
            });

            if(jsonObject != null){
                ((TextView)row.findViewById(R.id.tv_farmexpenseid)).setText(jsonObject.getString("Id"));
                ((EditText)row.findViewById(R.id.input_expensedesc)).setText(jsonObject.getString("Decscription"));
                ((EditText)row.findViewById(R.id.input_expenseamount)).setText(jsonObject.getString("Amount"));
                ((EditText)row.findViewById(R.id.input_expensedate)).setText(jsonObject.getString("RequestDate"));
            }

            tl_cropexpense.addView(row);
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void openCalendar(final EditText view){
        try{
            Calendar mcurrentDate = Calendar.getInstance();
            year = mcurrentDate.get(Calendar.YEAR);
            month = mcurrentDate.get(Calendar.MONTH);
            day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker =new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
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
                    //EditText et = tl_cropexpense.getChildAt(row_id).findViewById(R.id.input_expensedate);
                    view.setText(String.valueOf(year)+"-"+smonth+"-"+sday);
                }
            },year, month, day);
            mDatePicker.setTitle("Please select date");
            // TODO Hide Future Date Here
            //mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

            // TODO Hide Past Date Here
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            mDatePicker.show();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btn_save:
                    validateAndSaveData();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    break;
                case R.id.fab_add_newrow:
                    if(validateTableRows())
                        addNewRowToTable(null);
                    break;
                case R.id.img_startcalendar:
                    openCalendar(input_startdate);
                    break;
                case R.id.img_endcalendar:
                    openCalendar(input_enddate);
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    private void validateAndSaveData(){
        try{
            if(spnr_croptype.getSelectedItem().toString().trim().equalsIgnoreCase("Select")){
                ((TextView)spnr_croptype.getSelectedView()).setError("Select crop type");
                return;
            }

            if(input_landacers.getText().toString().trim().equalsIgnoreCase("")){
                input_layout_landacers.setError("Enter land area");
                requestFocus(input_landacers);
                return;
            }else {
                input_layout_landacers.setErrorEnabled(false);
            }

            if(spnr_profitshare.getSelectedItem().toString().trim().equalsIgnoreCase("Select")){
                ((TextView)spnr_profitshare.getSelectedView()).setError("Select profit share");
                return;
            }

            if(input_startdate.getText().toString().trim().equalsIgnoreCase("")){
                input_layout_startdate.setError("Select start date");
                requestFocus(input_startdate);
                return;
            }else {
                input_layout_startdate.setErrorEnabled(false);
            }

            if(input_enddate.getText().toString().trim().equalsIgnoreCase("")){
                input_layout_enddate.setError("Select end date");
                requestFocus(input_enddate);
                return;
            }else {
                input_layout_enddate.setErrorEnabled(false);
            }

            if(!validateTableRows())
                return;

            expenseData.put("agent_id", Integer.parseInt(dbHelper.getParameter("user_id")));
            expenseData.put("farm_id", farm_id);
            expenseData.put("farm_expense_id", farm_expense_id);
            expenseData.put("crop_type", spnr_croptype.getSelectedItem().toString().trim());
            expenseData.put("land_acers", Float.parseFloat(input_landacers.getText().toString().trim()));
            expenseData.put("profit_share", Float.parseFloat(spnr_profitshare.getSelectedItem().toString().trim()));
            expenseData.put("start_date", input_startdate.getText().toString().trim());
            expenseData.put("end_date", input_enddate.getText().toString().trim());

            JSONArray expense = new JSONArray();
            for(int i= 1; i <  tl_cropexpense.getChildCount(); i++){
                String exp_id = ((TextView)tl_cropexpense.getChildAt(i).findViewById(R.id.tv_farmexpenseid)).getText().toString().trim();
                String exp_desc = ((EditText)tl_cropexpense.getChildAt(i).findViewById(R.id.input_expensedesc)).getText().toString().trim();
                String exp_amount = ((EditText)tl_cropexpense.getChildAt(i).findViewById(R.id.input_expenseamount)).getText().toString().trim();
                String exp_date = ((EditText)tl_cropexpense.getChildAt(i).findViewById(R.id.input_expensedate)).getText().toString().trim();
                JSONObject item = new JSONObject();
                item.put("Id", exp_id.equalsIgnoreCase("")?0:Integer.parseInt(exp_id));
                item.put("Decscription", exp_desc);
                item.put("Amount", Float.parseFloat(exp_amount));
                item.put("RequestDate", exp_date);
                expense.put(item);
            }
            expenseData.put("expenses", expense);

            String finalSHA1 = getSHA1(expenseData.toString());

            //Do not the data back to server if nothing has changed or data is empty
            if(finalSHA1.equalsIgnoreCase(expenseDataSHA1) || finalSHA1.equalsIgnoreCase(getSHA1("{}")))
                goBack();
            else
                new putFarmExpenseDetails().execute(expenseData.toString(), dbHelper.getParameter("token"));
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public class getFarmExpenseDetails extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_save.setText("");
        }

        @Override
        protected JSONObject doInBackground(String[] params) {
            //android.os.Debug.waitForDebugger();
            JSONObject response = null;
            try {
                    response = wso.MakeGetCall("FarmData/getExpenseReport?Id="+params[0], params[1]);
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
            btn_save.setText("Save");
            try {
                if (result.getInt("responseCode") == 200) {
                    JSONObject response = new JSONObject(result.getString("response"));
                    if(response.getString("result").equalsIgnoreCase("")) {
                        expenseData = new JSONObject();
                        expenseDataSHA1 = getSHA1(expenseData.toString());
                    }
                    else {
                        expenseData = new JSONObject(response.getString("result"));
                        expenseDataSHA1 = getSHA1(expenseData.toString());
                    }
                    fillView(expenseData);
                } else if(result.getInt("responseCode") == 401){
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Session expired", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Something wrong, we will fix it.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        }
    }

    private String getSHA1(String input){
        String output = "";
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(input.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes)
            {
                buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            return buffer.toString();
        }catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
        return output;
    }

    public class putFarmExpenseDetails extends AsyncTask<String, Integer, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_save.setText("");
        }

        @Override
        protected JSONObject doInBackground(String[] params) {
            //android.os.Debug.waitForDebugger();
            JSONObject response = null;
            try {
                response = wso.MakePostCall("FarmData/putExpenseReport", params[0], params[1]);
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
            btn_save.setText("Save");
            try {
                if (result.getInt("responseCode") == 200) {
                    goBack();
                } else if(result.getInt("responseCode") == 401){
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Session expired", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(getActivity().findViewById(R.id.fab), "Something wrong, we will fix it.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            catch (Exception e){logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage());}
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