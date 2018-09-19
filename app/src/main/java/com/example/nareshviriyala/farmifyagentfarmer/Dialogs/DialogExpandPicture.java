package com.example.nareshviriyala.farmifyagentfarmer.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.R;

public class DialogExpandPicture extends Dialog implements View.OnClickListener{
    private LogErrors logErrors;
    private String className;
    private Context context;
    private byte[] imgsource;
    private ImageView img_exppicture, img_close;


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
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        img_exppicture = (ImageView) findViewById(R.id.img_exppicture);
        Bitmap bmp = BitmapFactory.decodeByteArray(imgsource, 0, imgsource.length);
        img_exppicture.setImageBitmap(bmp);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_close:
                dismiss();
                break;
        }
    }
}
