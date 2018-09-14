package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelSystemParameter {

    private String ParamName;
    private String ParamValue;

    public ModelSystemParameter(String ParamName, String ParamValue) {
        this.ParamName = ParamName;
        this.ParamValue = ParamValue;
    }


    public String getParamName() {
        return ParamName;
    }

    public String getParamValue() {
        return ParamValue;
    }

}

