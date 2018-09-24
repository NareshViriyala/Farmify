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

    private int agronomicDataItemId = -2;

    public int getagronomicDataItemId() {
        return agronomicDataItemId;
    }
    public void setagronomicDataItemId(int agronomicDataItemId) {
        this.agronomicDataItemId = agronomicDataItemId;
    }

    /*
    private String device_id;
    public void setDevice_id(String device_id){this.device_id = device_id;}
    public String getDevice_id(){return device_id;}

    public void clearVariables(){
        userprofile = null;
    }
    */

}
