package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class FarmerData {

    private int mFarmerDataImage;
    private String mFarmerDataTypeName;
    private String mFarmerDataTypeDesc;
    private int mFarmerDataProgress;

    public FarmerData(int mFarmerDataImage, String mFarmerDataTypeName, String mFarmerDataTypeDesc, int mFarmerDataProgress) {
        this.mFarmerDataImage = mFarmerDataImage;
        this.mFarmerDataTypeName = mFarmerDataTypeName;
        this.mFarmerDataTypeDesc = mFarmerDataTypeDesc;
        this.mFarmerDataProgress = mFarmerDataProgress;
    }

    public int getmFarmerDataImage() {
        return mFarmerDataImage;
    }

    public void setmFarmerDataImage(int mFarmerDataImage) {
        this.mFarmerDataImage = mFarmerDataImage;
    }

    public String getmFarmerDataTypeName() {
        return mFarmerDataTypeName;
    }

    public void setmFarmerDataTypeName(String mFarmerDataTypeName) {
        this.mFarmerDataTypeName = mFarmerDataTypeName;
    }

    public String getmFarmerDataTypeDesc() {
        return mFarmerDataTypeDesc;
    }

    public void setmFarmerDataTypeDesc(String mFarmerDataTypeDesc) {
        this.mFarmerDataTypeDesc = mFarmerDataTypeDesc;
    }

    public int getmFarmerDataProgress() {
        return mFarmerDataProgress;
    }

    public void setmFarmerDataProgress(int mFarmerDataProgress) {
        this.mFarmerDataProgress = mFarmerDataProgress;
    }

}
