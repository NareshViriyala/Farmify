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

import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddPartnerItem;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFPartner;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelPartnerInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AdapterPartnerInformation extends ArrayAdapter<ModelPartnerInformation> implements View.OnClickListener{

    private Context context;
    private List<ModelPartnerInformation> partnerData;
    private LogErrors logErrors;
    private String className;
    private FragmentAFPartner fragmentPartner;

    public AdapterPartnerInformation(Context context, ArrayList<ModelPartnerInformation> list, FragmentAFPartner fragmentPartner){
        super(context, 0, list);
        try {
            this.context = context;
            this.fragmentPartner = fragmentPartner;
            logErrors = LogErrors.getInstance(context);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            this.partnerData = list;
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void updatePartnerList(List<ModelPartnerInformation> newlist) {
        partnerData.clear();
        partnerData.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent){
        View listItem = view;
        try{
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.listitem_partnerinformation,parent,false);

            ModelPartnerInformation currentDataItem = partnerData.get(position);

            ImageView img_assetedit = (ImageView)listItem.findViewById(R.id.img_assetedit);
            ImageView img_assetdelete = (ImageView)listItem.findViewById(R.id.img_assetdelete);

            img_assetedit.setOnClickListener(this);
            img_assetdelete.setOnClickListener(this);

            TextView tv_assetid = (TextView)listItem.findViewById(R.id.tv_assetid);
            tv_assetid.setText(String.valueOf(currentDataItem.getId()));

            TextView textView_assetname = (TextView)listItem.findViewById(R.id.textView_assetname);
            textView_assetname.setText(currentDataItem.getPartnerName());

            TextView textView_assetvalue = (TextView)listItem.findViewById(R.id.textView_assetvalue);
            textView_assetvalue.setText(currentDataItem.getPartnerPhone());

            TextView textView_partnertype = (TextView)listItem.findViewById(R.id.textView_partnertype);
            textView_partnertype.setText(currentDataItem.getPartnerType());

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

        return listItem;
    }

    @Override
    public void onClick(View v) {
        try {
            View parent = (View) v.getParent();
            TextView tv_id = parent.findViewById(R.id.tv_assetid);
            int Id = Integer.parseInt(tv_id.getText().toString().trim());
            switch (v.getId()) {
                case R.id.img_assetedit:
                    ModelPartnerInformation item = null;
                    for(int i = 0; i < partnerData.size(); i++){
                        if(partnerData.get(i).getId() == Id)
                            item = partnerData.get(i);
                    }
                    new DialogAddPartnerItem(context, item, fragmentPartner).show();
                    break;
                case R.id.img_assetdelete:
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
                                JSONArray oldlist = fragmentPartner.farmerpartnerData;
                                JSONArray newlist = new JSONArray();
                                for (int i = 0; i < oldlist.length(); i++) {
                                    if (Id != oldlist.getJSONObject(i).getInt("Id"))
                                        newlist.put(oldlist.get(i));
                                }
                                fragmentPartner.farmerpartnerData = newlist;
                                fragmentPartner.refreshPartnerListView();

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
