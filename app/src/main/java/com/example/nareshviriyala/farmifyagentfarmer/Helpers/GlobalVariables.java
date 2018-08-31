package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import org.json.JSONObject;

public class GlobalVariables {

    private static GlobalVariables globalVariables;

    private GlobalVariables(){}  //private constructor

    public static GlobalVariables getInstance(){
        if (globalVariables == null){ //if there is no instance available... create new one
            globalVariables = new GlobalVariables();
        }
        return globalVariables;
    }

    /*private JSONObject userprofile;
    public JSONObject getUserProfile() {
        return userprofile;
    }
    public void setUserProfile(JSONObject userprofile) {
        this.userprofile = userprofile;
    }

    private String device_id;
    public void setDevice_id(String device_id){this.device_id = device_id;}
    public String getDevice_id(){return device_id;}

    public void clearVariables(){
        userprofile = null;
    }
    */

}
