package com.example.nareshviriyala.farmifyagentfarmer.Models;

public class ModelCreditInformation {

    private int Id;
    private String Source;
    private String Date;
    private String Amount;
    private String Interest;
    private Boolean Paid;
    private String PendingAmount;

    public ModelCreditInformation(int Id, String Source, String Date, String Amount, String Interest, Boolean Paid, String PendingAmount) {
        this.Id = Id;
        this.Source = Source;
        this.Date = Date;
        this.Amount = Amount;
        this.Interest = Interest;
        this.Paid = Paid;
        this.PendingAmount = PendingAmount;
    }
    public int getId() {return this.Id;}
    public String getSource() {
        return this.Source;
    }
    public String getDate() {
        return this.Date;
    }
    public String getAmount() {return this.Amount;}
    public String getInterest() {
        return this.Interest;
    }
    public Boolean getPaid() {
        return this.Paid;
    }
    public String getPendingAmount() {
        return this.PendingAmount;
    }
}
