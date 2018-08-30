package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogErrors {

    private static LogErrors logErrors;
    private GlobalVariables globalVariables;
    private WebServiceOperation wso;
    private LogErrors(){
        globalVariables = GlobalVariables.getInstance();
        wso = new WebServiceOperation();
    }  //private constructor

    public static LogErrors getInstance(){
        if (logErrors == null){ //if there is no instance available... create new one
            logErrors = new LogErrors();
        }
        return logErrors;
    }

    public void WriteLog(String className, String methodName, String error){
        try {
            int user_id = 0, login_id = 0;
            if(globalVariables.getUserProfile().has("id"))
                user_id = globalVariables.getUserProfile().getInt("id");
            if(globalVariables.getUserProfile().has("log_id"))
                login_id = globalVariables.getUserProfile().getInt("log_id");
            JSONObject errorlog = new JSONObject();
            errorlog.put("user_id", user_id).put("login_id",login_id).put("code_file",className).put("method_name",methodName).put("error_desc", error);
            JSONArray jarray = new JSONArray();
            jarray.put(errorlog);
            JSONObject payload = new JSONObject();
            payload.put("error_log", jarray);
            new callLogErrorDataAPI().execute(payload);
        }catch (JSONException e) {
        }catch (Exception ex){
        }
    }

    public class callLogErrorDataAPI extends AsyncTask<JSONObject, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... jsondata) {
            JSONObject response = new JSONObject();
            try {
                response = wso.MakePostCall("Logging/logerrordetails", jsondata[0].toString());
            }catch (Exception ex){}
            return response;
        }
    }
}
