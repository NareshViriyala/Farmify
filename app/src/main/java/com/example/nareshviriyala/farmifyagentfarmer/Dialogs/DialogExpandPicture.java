package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Fragments.FragmentAFPartner;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Models.ModelPartnerInformation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class DialogExpandPicture extends Dialog{
    private LogErrors logErrors;
    private String className;
    private Context context;
    private byte[] imgsource;
    private ImageView img_exppicture;


    public DialogExpandPicture(@NonNull Context context, byte[] imgsource) {
        super(context);
        try {
            this.context = context;
            this.imgsource = imgsource;
            logErrors = LogErrors.getInstance(context);
            className = new Object() {
            }.getClass().getEnclosingClass().getName();
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_expandpicture);

        img_exppicture = (ImageView) findViewById(R.id.img_exppicture);
        Bitmap bmp = BitmapFactory.decodeByteArray(imgsource, 0, imgsource.length);
        img_exppicture.setImageBitmap(bmp);
    }

}
