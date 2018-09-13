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

import com.example.nareshviriyala.farmifyagentfarmer.Dialogs.DialogAddCreditItem;
import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFCommerce;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelCreditInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class AdapterCreditInformation extends ArrayAdapter<ModelCreditInformation> implements View.OnClickListener{
    private Context context;
    private List<ModelCreditInformation> creditData;
    private LogErrors logErrors;
    private String className;
    private FragmentAFCommerce fragmentCommerce;

    public AdapterCreditInformation(Context context, ArrayList<ModelCreditInformation> list, FragmentAFCommerce fragmentCommerce){
        super(context, 0, list);
        try {
            this.context = context;
            this.fragmentCommerce = fragmentCommerce;
            logErrors = LogErrors.getInstance(context);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            this.creditData = list;
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void updateCreditList(List<ModelCreditInformation> newlist) {
        creditData.clear();
        creditData.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent){
        View listItem = view;
        try{
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.listitem_creditinformation,parent,false);

            ModelCreditInformation currentDataItem = creditData.get(position);

            ImageView img_creditedit = (ImageView)listItem.findViewById(R.id.img_creditedit);
            ImageView img_creditdelete = (ImageView)listItem.findViewById(R.id.img_creditdelete);

            img_creditedit.setOnClickListener(this);
            img_creditdelete.setOnClickListener(this);

            TextView tv_creditid = (TextView)listItem.findViewById(R.id.tv_creditid);
            tv_creditid.setText(String.valueOf(currentDataItem.getId()));

            TextView textView_creditamount = (TextView)listItem.findViewById(R.id.textView_creditamount);
            textView_creditamount.setText("Amount : Rs."+currentDataItem.getAmount()+" ("+currentDataItem.getInterest()+"%)");

            TextView textView_creditdate = (TextView)listItem.findViewById(R.id.textView_creditdate);
            textView_creditdate.setText("Date : "+currentDataItem.getDate());

            TextView textView_creditsrc = (TextView)listItem.findViewById(R.id.textView_creditsrc);
            textView_creditsrc.setText("Source : "+currentDataItem.getSource());

            TextView textView_creditinterest = (TextView)listItem.findViewById(R.id.textView_creditinterest);
            textView_creditinterest.setText("Pending : "+currentDataItem.getPendingAmount());
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

        return listItem;
    }

    @Override
    public void onClick(View v) {
        try {
            View parent = (View) v.getParent();
            TextView tv_id = parent.findViewById(R.id.tv_creditid);
            int Id = Integer.parseInt(tv_id.getText().toString().trim());
            switch (v.getId()) {
                case R.id.img_creditedit:
                    ModelCreditInformation item = null;
                    for(int i = 0; i < creditData.size(); i++){
                        if(creditData.get(i).getId() == Id)
                            item = creditData.get(i);
                    }
                    new DialogAddCreditItem(context, item, fragmentCommerce).show();
                    break;
                case R.id.img_creditdelete:
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
                                JSONArray oldlist = fragmentCommerce.farmercommerceData.getJSONArray("CreditInformation");
                                JSONArray newlist = new JSONArray();
                                for (int i = 0; i < oldlist.length(); i++) {
                                    if (Id != oldlist.getJSONObject(i).getInt("Id"))
                                        newlist.put(oldlist.get(i));
                                }
                                fragmentCommerce.farmercommerceData.put("CreditInformation", newlist);
                                fragmentCommerce.refreshCreditListView();

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
