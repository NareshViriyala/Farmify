package com.example.nareshviriyala.farmifyagentfarmer.Helpers;


import android.content.Context;
import android.util.Base64;
import android.util.JsonWriter;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Convertor {
    private Context context;
    private LogErrors logErrors;
    private String className;

    public Convertor(Context context){
        this.context = context;
        logErrors = LogErrors.getInstance(context);
        className = new Object() {}.getClass().getEnclosingClass().getName();
    }

    public byte[] convertStringToByteArray(String input){
        byte[] output = null;
        try {
            output = input.getBytes("UTF-8");
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return output;
    }

    public String convertByteArrayToString(byte[] input){
        String output = null;
        try{
            output = new String(input, "UTF-8");
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return output;
    }
}