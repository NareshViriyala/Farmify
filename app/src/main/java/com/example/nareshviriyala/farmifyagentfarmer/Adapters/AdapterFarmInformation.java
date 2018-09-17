package com.example.nareshviriyala.farmifyagentfarmer.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFAgronomic;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.DatabaseHelper;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelAgronomicInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class AdapterFarmInformation extends ArrayAdapter<ModelAgronomicInformation> implements View.OnClickListener{
    private Context context;
    private List<ModelAgronomicInformation> farmData;
    private LogErrors logErrors;
    private String className;
    private FragmentAFAgronomic fragmentAFAgronomic;
    private DatabaseHelper dbHelper;

    public AdapterFarmInformation(Context context, ArrayList<ModelAgronomicInformation> list, FragmentAFAgronomic fragmentAFAgronomic){
        super(context, 0, list);
        try {
            this.context = context;
            this.fragmentAFAgronomic = fragmentAFAgronomic;
            logErrors = LogErrors.getInstance(context);
            dbHelper = new DatabaseHelper(context);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            this.farmData = list;
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void updateFarmList(List<ModelAgronomicInformation> newlist) {
        farmData.clear();
        farmData.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent){
        View listItem = view;
        try{
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.listitem_farmdata,parent,false);

            ModelAgronomicInformation currentDataItem = farmData.get(position);

            ImageView img_deleteFarm = (ImageView)listItem.findViewById(R.id.img_deleteFarm);
            img_deleteFarm.setOnClickListener(this);

            TextView textView_id = listItem.findViewById(R.id.textView_id);
            textView_id.setText(String.valueOf(currentDataItem.getId()));

            TextView textView_farmertype = listItem.findViewById(R.id.textView_farmertype);
            textView_farmertype.setText("Farmer type: "+currentDataItem.getFarmerType());

            TextView textView_farmercategory = (TextView)listItem.findViewById(R.id.textView_farmercategory);
            textView_farmercategory.setText("Farmer category: "+currentDataItem.getFarmerCategory());

            TextView textView_croptype = (TextView)listItem.findViewById(R.id.textView_croptype);
            textView_croptype.setText("Crop type : "+currentDataItem.getCropType());

            TextView textView_soiltype = (TextView)listItem.findViewById(R.id.textView_soiltype);
            textView_soiltype.setText("Soil type : "+currentDataItem.getSoilType());

            TextView textView_watersource = (TextView)listItem.findViewById(R.id.textView_watersource);
            textView_watersource.setText("Water source : "+currentDataItem.getWaterSource());

            TextView textView_landacers = (TextView)listItem.findViewById(R.id.textView_landacers);
            textView_landacers.setText("Land : "+currentDataItem.getLandAcers());

            TextView textView_soiltesting = (TextView)listItem.findViewById(R.id.textView_soiltesting);
            textView_soiltesting.setText("Soil testing : "+(currentDataItem.getSoilTesting()?"Done":"Not done"));

            TextView textView_cropinsurance = (TextView)listItem.findViewById(R.id.textView_cropinsurance);
            textView_cropinsurance.setText("Crop insurance : "+(currentDataItem.getCropInsurance()?"Taken":"Not taken"));

            TextView textView_farmexp = (TextView)listItem.findViewById(R.id.textView_farmexp);
            textView_farmexp.setText("Farming experience : "+currentDataItem.getFarmExp()+" years");

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

        return listItem;
    }

    @Override
    public void onClick(View v) {
        try {
            View parent = (View) v.getParent();
            TextView tv_id = parent.findViewById(R.id.textView_id);
            int Id = Integer.parseInt(tv_id.getText().toString().trim());
            switch (v.getId()) {
                case R.id.img_deleteFarm:
                    deleteItem(Id);
                    break;
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void deleteItem(int sId){
        final int Id = sId;
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                dialog.cancel();
                                JSONArray oldlist = new JSONArray(dbHelper.getParameter(context.getResources().getString(R.string.Agronomic)));
                                JSONArray newlist = new JSONArray();
                                for (int i = 0; i < oldlist.length(); i++) {
                                    if (Id != oldlist.getJSONObject(i).getInt("Id"))
                                        newlist.put(oldlist.get(i));
                                }
                                dbHelper.setParameter(context.getResources().getString(R.string.Agronomic), newlist.toString());
                                fragmentAFAgronomic.refreshFarmList();

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
}
