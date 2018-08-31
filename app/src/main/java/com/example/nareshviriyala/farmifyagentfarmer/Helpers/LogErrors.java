package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LogErrors {

    private static LogErrors logErrors;
    private GlobalVariables globalVariables;
    private WebServiceOperation wso;
    private static Context context;
    private DatabaseHelper dbHeler;
    private String token;

    private LogErrors(){
        globalVariables = GlobalVariables.getInstance();
        wso = new WebServiceOperation();
        dbHeler = new DatabaseHelper(context);
    }  //private constructor

    public static LogErrors getInstance(Context mcontext){
        if (logErrors == null){ //if there is no instance available... create new one
            context = mcontext;
            logErrors = new LogErrors();
        }
        return logErrors;
    }

    public void WriteLog(String className, String methodName, String error){
        try {
            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            int user_id = 0, signin_id = 0;
            String user_id_str = dbHeler.getParameter("user_id");
            String signin_id_str = dbHeler.getParameter("signin_id");
            token = dbHeler.getParameter("token");
            if(!user_id_str.isEmpty())
                user_id = Integer.parseInt(user_id_str);
            if(!signin_id_str.isEmpty())
                signin_id = Integer.parseInt(signin_id_str);
            JSONObject errorlog = new JSONObject();
            errorlog.put("user_id", user_id)
                    .put("login_id",signin_id)
                    .put("code_file",className)
                    .put("method_name",methodName)
                    .put("error_desc", error);
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
                response = wso.MakePostCall("Logging/logerrordetails", jsondata[0].toString(), token);
            }catch (Exception ex){}
            return response;
        }
    }
}
