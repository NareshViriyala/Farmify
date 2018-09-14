package com.example.nareshviriyala.farmifyagentfarmer.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelFarmerData;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterFarmerData extends ArrayAdapter<ModelFarmerData>{
    private Context context;
    private List<ModelFarmerData> farmerData = new ArrayList<>();

    public AdapterFarmerData(Context context, ArrayList<ModelFarmerData> list){
        super(context, 0, list);
        this.context = context;
        this.farmerData = list;
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent){
        View listItem = view;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.listitem_farmerdata,parent,false);

        ModelFarmerData currentDataItem = farmerData.get(position);

        ImageView img_icon = (ImageView)listItem.findViewById(R.id.img_icon);
        img_icon.setImageResource(currentDataItem.getmFarmerDataImage());

        TextView textView_datatype = (TextView)listItem.findViewById(R.id.textView_datatype);
        textView_datatype.setText(currentDataItem.getmFarmerDataTypeName());

        TextView textView_datadesc = (TextView)listItem.findViewById(R.id.textView_datadesc);
        textView_datadesc.setText(currentDataItem.getmFarmerDataTypeDesc());

        if(currentDataItem.getmFarmerDataProgress() != 0){
            ImageView img_progress = (ImageView)listItem.findViewById(R.id.img_progress);
            if(currentDataItem.getmFarmerDataProgress() == 1) { //not complete
                img_progress.setImageResource(R.drawable.baseline_clear_black_24dp);
                img_progress.setColorFilter(ContextCompat.getColor(context, R.color.error));
            }else if(currentDataItem.getmFarmerDataProgress() == 2){ //partially done
                img_progress.setImageResource(R.drawable.baseline_done_black_24dp);
                img_progress.setColorFilter(ContextCompat.getColor(context, R.color.lightblue));
            }else if(currentDataItem.getmFarmerDataProgress() == 3){ //fully done
                img_progress.setImageResource(R.drawable.baseline_done_all_black_24dp);
                img_progress.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
        return listItem;
    }
}
