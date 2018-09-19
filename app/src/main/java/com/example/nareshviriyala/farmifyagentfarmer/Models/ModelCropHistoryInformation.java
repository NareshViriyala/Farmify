package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelCropHistoryInformation {

    private int Id;
    private String Year;
    private String Month;
    private String Acers;
    private String Crop;
    private String Production;
    private String Rate;

    public ModelCropHistoryInformation(int Id, String Year, String Month, String Acers, String Crop, String Production, String Rate) {
        this.Id = Id;
        this.Year = Year;
        this.Month = Month;
        this.Acers = Acers;
        this.Crop = Crop;
        this.Production = Production;
        this.Rate = Rate;
    }
    public int getId() {return this.Id;}
    public String getYear() {return this.Year;}
    public String getMonth() {return this.Month;}
    public String getAcers() {return this.Acers;}
    public String getCrop() {return this.Crop;}
    public String getProduction() {return this.Production;}
    public String getRate() {return this.Rate;}
}
