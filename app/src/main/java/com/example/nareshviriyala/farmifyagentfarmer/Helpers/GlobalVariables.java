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

    private JSONObject userprofile;

    public JSONObject getUserProfile() {
        return userprofile;
    }
    public void setUserProfile(JSONObject userprofile) {
        this.userprofile = userprofile;
    }



    public void clearVariables(){
        userprofile = null;
    }

}
