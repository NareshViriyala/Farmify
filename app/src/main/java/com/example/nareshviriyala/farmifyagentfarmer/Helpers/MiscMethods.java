package com.example.nareshviriyala.farmifyagentfarmer.Helpers;


import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

public class MiscMethods {
    private Context context;
    private LogErrors logErrors;
    private String className;

    public MiscMethods(Context context){
        this.context = context;
        logErrors = LogErrors.getInstance(context);
        className = new Object() {}.getClass().getEnclosingClass().getName();
    }

    public JSONArray deleteItem(JSONArray inJArray, int position){
        JSONArray outJArray = new JSONArray();
        try {
            for(int i = 0; i < inJArray.length(); i++){
                if(i != position){
                    JSONObject item = inJArray.getJSONObject(i);
                    outJArray.put(item);
                }
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
        return outJArray;
    }
}