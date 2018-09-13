package com.example.nareshviriyala.farmifyagentfarmer.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFImages;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelImageInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class AdapterImageInformation extends ArrayAdapter<ModelImageInformation> {

    private Context context;
    private List<ModelImageInformation> imageList;
    private LogErrors logErrors;
    private String className;

    public AdapterImageInformation(Context context, ArrayList<ModelImageInformation> list){
        super(context, 0, list);
        try {
            this.context = context;
            logErrors = LogErrors.getInstance(context);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
            this.imageList = list;
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void updateimageList(List<ModelImageInformation> newlist) {
        imageList.clear();
        imageList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent){
        View listItem = view;
        try{
            if(listItem == null)
                listItem = LayoutInflater.from(context).inflate(R.layout.listitem_imageinformation,parent,false);

            ModelImageInformation currentDataItem = imageList.get(position);

            ImageView img_picture = (ImageView)listItem.findViewById(R.id.img_picture);

            if(currentDataItem.getImageSource() != null && currentDataItem.getImageSource().size() > 0){
                byte[] img = currentDataItem.getImageSource().get(0);
                Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
                img_picture.setImageBitmap(bmp);
            }


            TextView tv_picturetype = (TextView)listItem.findViewById(R.id.tv_picturetype);
            tv_picturetype.setText(currentDataItem.getImageType());

            TextView tv_pictureid = (TextView)listItem.findViewById(R.id.tv_pictureid);
            tv_pictureid.setText(String.valueOf(currentDataItem.getId()));

        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }

        return listItem;
    }
}
