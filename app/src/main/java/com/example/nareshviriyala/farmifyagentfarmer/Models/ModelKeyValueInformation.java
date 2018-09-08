package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelKeyValueInformation {

    private int Id;
    private String AssetName;
    private String AssetValue;

    public ModelKeyValueInformation(int Id, String AssetName, String AssetValue) {
        this.Id = Id;
        this.AssetName = AssetName;
        this.AssetValue = AssetValue;
    }
    public int getId() {return this.Id;}
    public String getAssetName() {
        return this.AssetName;
    }
    public String getAssetValue() {
        return this.AssetValue;
    }
}
