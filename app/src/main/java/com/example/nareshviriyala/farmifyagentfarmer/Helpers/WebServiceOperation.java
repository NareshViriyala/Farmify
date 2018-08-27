package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebServiceOperation {

    private String apiURL;

    public WebServiceOperation(){
        apiURL = "http://ec2-18-213-3-218.compute-1.amazonaws.com/";
    }

    public String MakeGetCall(){
       return "";
    }

    public JSONObject MakePostCall(String endPoint, String postBody){
        JSONObject response = new JSONObject();
        try {
            URL url = new URL(apiURL+endPoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Method", "POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(postBody.getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            BufferedReader in;

            if(responseCode == HttpURLConnection.HTTP_OK){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }else{
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuffer sb = new StringBuffer("");
            String line = "";
            while ((line = in.readLine()) != null){
                sb.append(line);
                break;
            }
            in.close();

            response.put("responseCode", responseCode);
            response.put("response", sb.toString());
        }catch(Exception e){
            try {
                response.put("responseCode", 500);
                response.put("response", e.getMessage());
            }catch(Exception ex){}
        }
        return response;
    }
}
