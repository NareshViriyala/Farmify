package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelPartnerInformation {

    private int Id;
    private String PartnerName;
    private String PartnerPhone;
    private String PartnerType;


    public ModelPartnerInformation(int Id, String PartnerName, String PartnerPhone, String PartnerType) {
        this.Id = Id;
        this.PartnerName = PartnerName;
        this.PartnerPhone = PartnerPhone;
        this.PartnerType = PartnerType;
    }
    public int getId() {return this.Id;}
    public String getPartnerName() {
        return this.PartnerName;
    }
    public String getPartnerPhone() {
        return this.PartnerPhone;
    }
    public String getPartnerType() {
        return this.PartnerType;
    }
}
