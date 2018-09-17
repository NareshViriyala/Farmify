package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelAgronomicInformation {

    private int Id;
    private String FarmerType;
    private String FarmerCategory;
    private String CropType;
    private String SoilType;
    private String WaterSource;
    private String LandAcers;
    private boolean SoilTesting;
    private boolean CropInsurance;
    private String FarmExp;


    public ModelAgronomicInformation(int Id, String FarmerType, String FarmerCategory, String CropType
            , String SoilType, String WaterSource, String LandAcers, boolean SoilTesting
            , boolean CropInsurance, String FarmExp) {
        this.Id = Id;
        this.FarmerType = FarmerType;
        this.FarmerCategory = FarmerCategory;
        this.CropType = CropType;
        this.SoilType = SoilType;
        this.WaterSource = WaterSource;
        this.LandAcers = LandAcers;
        this.SoilTesting = SoilTesting;
        this.CropInsurance = CropInsurance;
        this.FarmExp = FarmExp;
    }
    public int getId() {return this.Id;}
    public String getFarmerType() {
        return this.FarmerType;
    }
    public String getFarmerCategory() {
        return this.FarmerCategory;
    }
    public String getCropType() {return this.CropType;}
    public String getSoilType() {
        return this.SoilType;
    }
    public String getWaterSource() {
        return this.WaterSource;
    }
    public String getLandAcers() {
        return this.LandAcers;
    }
    public boolean getSoilTesting() {
        return this.SoilTesting;
    }
    public boolean getCropInsurance() {
        return this.CropInsurance;
    }
    public String getFarmExp() {
        return this.FarmExp;
    }

}
