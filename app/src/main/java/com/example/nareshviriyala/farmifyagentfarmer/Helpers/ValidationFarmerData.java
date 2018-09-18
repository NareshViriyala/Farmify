package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import android.content.Context;

import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONArray;
import org.json.JSONObject;


public class ValidationFarmerData {

    private Context context;
    private LogErrors logErrors;
    private String className;
    private DatabaseHelper dbHelper;

    public ValidationFarmerData(Context context){
        this.context = context;
        logErrors = LogErrors.getInstance(context);
        className = new Object(){}.getClass().getEnclosingClass().getName();
        dbHelper = new DatabaseHelper(context);
    }

    public void validateIndividualData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Individual));
            JSONObject farmerIdvData = new JSONObject(data);
            dbHelper.setParameter(context.getResources().getString(R.string.IndividualStatus), "3");
            if(!farmerIdvData.has("Aadhar") || !farmerIdvData.has("Phone")) {
                dbHelper.setParameter(context.getResources().getString(R.string.IndividualStatus), "1");
            }else if(!farmerIdvData.has("FirstName") || farmerIdvData.getString("FirstName").equalsIgnoreCase("")
                    || !farmerIdvData.has("DOB") || farmerIdvData.getString("DOB").equalsIgnoreCase("")
                    || !farmerIdvData.has("Caste") || farmerIdvData.getString("Caste").equalsIgnoreCase("")
                    || !farmerIdvData.has("Gender") || farmerIdvData.getString("Gender").equalsIgnoreCase("")
                    || !farmerIdvData.has("Address1") || farmerIdvData.getString("Address1").equalsIgnoreCase("")
                    || !farmerIdvData.has("State") || farmerIdvData.getString("State").equalsIgnoreCase("")
                    || !farmerIdvData.has("District") || farmerIdvData.getString("District").equalsIgnoreCase("")
                    || !farmerIdvData.has("Pincode") || farmerIdvData.getString("Pincode").equalsIgnoreCase("")){
                dbHelper.setParameter(context.getResources().getString(R.string.IndividualStatus), "1");
            }else if(!farmerIdvData.has("LastName") || farmerIdvData.getString("LastName").equalsIgnoreCase("")
                    || !farmerIdvData.has("Surname") || farmerIdvData.getString("Surname").equalsIgnoreCase("")
                    || !farmerIdvData.has("Address2") || farmerIdvData.getString("Address2").equalsIgnoreCase("")
                    || !farmerIdvData.has("VillageTown") || farmerIdvData.getString("VillageTown").equalsIgnoreCase("")){
                dbHelper.setParameter(context.getResources().getString(R.string.IndividualStatus), "2");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateBankData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Bank));
            JSONObject farmerbankData = new JSONObject(data);
            dbHelper.setParameter(context.getResources().getString(R.string.BankStatus), "3");
            if(!farmerbankData.has("AccountName") || farmerbankData.getString("AccountName").equalsIgnoreCase("")
                    || !farmerbankData.has("AccountNumber") || farmerbankData.getString("AccountNumber").equalsIgnoreCase("")
                    || !farmerbankData.has("BankName") || farmerbankData.getString("BankName").equalsIgnoreCase("")
                    || !farmerbankData.has("IFSC") || farmerbankData.getString("IFSC").equalsIgnoreCase("")
                    || !farmerbankData.has("AccountType") || farmerbankData.getString("AccountType").equalsIgnoreCase("")){
                dbHelper.setParameter(context.getResources().getString(R.string.BankStatus), "1");
            }else if(!farmerbankData.has("BranchName") || farmerbankData.getString("BranchName").equalsIgnoreCase("")
                    || !farmerbankData.has("District") || farmerbankData.getString("District").equalsIgnoreCase("")
                    || !farmerbankData.has("State") || farmerbankData.getString("State").equalsIgnoreCase("")){
                dbHelper.setParameter(context.getResources().getString(R.string.BankStatus), "2");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateSocialData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Social));
            JSONObject farmersocialData = new JSONObject(data);
            dbHelper.setParameter(context.getResources().getString(R.string.SocialStatus), "3");
            if(!farmersocialData.has("RationCard") || farmersocialData.getString("RationCard").equalsIgnoreCase("")
                    || !farmersocialData.has("PanCard") || farmersocialData.getString("PanCard").equalsIgnoreCase("")
                    || (farmersocialData.has("FacebookID") && farmersocialData.getString("FacebookID").equalsIgnoreCase(""))
                    || (farmersocialData.has("WhatsappID") && farmersocialData.getString("WhatsappID").equalsIgnoreCase(""))
                    || !farmersocialData.has("PanCard") || farmersocialData.getString("PanCard").equalsIgnoreCase("")
                    || !farmersocialData.has("Languages") || farmersocialData.getString("Languages").equalsIgnoreCase("")
                    || !farmersocialData.has("SourceInformation") || farmersocialData.getString("SourceInformation").equalsIgnoreCase("")
                    || !farmersocialData.has("SocialMediaInformation") || farmersocialData.getString("SocialMediaInformation").equalsIgnoreCase("")){
                dbHelper.setParameter(context.getResources().getString(R.string.SocialStatus), "2");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateAgromicData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Agronomic));
            if(data.isEmpty() || data == null || data.equalsIgnoreCase("[]")) {
                dbHelper.setParameter(context.getResources().getString(R.string.AgronomicStatus), "0");
            }
            else {
                dbHelper.setParameter(context.getResources().getString(R.string.AgronomicStatus), "3");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateCommerceData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Commerce));
            JSONObject farmercommerceData = new JSONObject(data);
            dbHelper.setParameter(context.getResources().getString(R.string.CommerceStatus), "3");
            if(!farmercommerceData.has("AnnualIncome") || farmercommerceData.getString("CropIncome").equalsIgnoreCase("")
                    || !farmercommerceData.has("FarmExpenseSource")
                    || (farmercommerceData.has("FarmExpenseSource") && farmercommerceData.getString("FarmExpenseSource").equalsIgnoreCase(""))){
                dbHelper.setParameter(context.getResources().getString(R.string.CommerceStatus), "1");
            }else if((farmercommerceData.has("CreditInformation") && farmercommerceData.getString("CreditInformation").equalsIgnoreCase(""))
                    || (farmercommerceData.has("AssetInformation") && farmercommerceData.getString("AssetInformation").equalsIgnoreCase(""))
                    || (farmercommerceData.has("ReferenceInformation") && farmercommerceData.getString("ReferenceInformation").equalsIgnoreCase(""))){
                dbHelper.setParameter(context.getResources().getString(R.string.CommerceStatus), "2");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validatePartnerData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Partner));
            if(data.isEmpty() || data == null || data.equalsIgnoreCase("[]")) {
                dbHelper.setParameter(context.getResources().getString(R.string.PartnerStatus), "0");
            }
            else {
                dbHelper.setParameter(context.getResources().getString(R.string.PartnerStatus), "3");
            }
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateImageData(){
        try{
            String data = dbHelper.getParameter(context.getResources().getString(R.string.Images));
            if(data.equalsIgnoreCase("") || data == null){
                dbHelper.setParameter(context.getResources().getString(R.string.ImagesStatus), "0");
                return;
            }
            JSONObject farmerImageData = new JSONObject(data);
            dbHelper.setParameter(context.getResources().getString(R.string.ImagesStatus), "3");
            if(!farmerImageData.has("Farmer") || farmerImageData.getJSONArray("Farmer").length() == 0
                    || !farmerImageData.has("Aadharcard")  || farmerImageData.getJSONArray("Aadharcard").length() == 0)
                dbHelper.setParameter(context.getResources().getString(R.string.ImagesStatus), "1");
            else if(farmerImageData.has("Farmer") && farmerImageData.getJSONArray("Farmer").length() > 0
                    && farmerImageData.has("Aadharcard")  && farmerImageData.getJSONArray("Aadharcard").length() > 0
                    && (!farmerImageData.has("Bankbook")  || (farmerImageData.has("Bankbook") && farmerImageData.getJSONArray("Bankbook").length() == 0)
                    || !farmerImageData.has("Rationcard") || (farmerImageData.has("Rationcard") && farmerImageData.getJSONArray("Rationcard").length() == 0)
                    || !farmerImageData.has("Pancard") || (farmerImageData.has("Pancard") && farmerImageData.getJSONArray("Pancard").length() == 0)
                    || !farmerImageData.has("Additional") || (farmerImageData.has("Additional") && farmerImageData.getJSONArray("Additional").length() == 0)))
                dbHelper.setParameter(context.getResources().getString(R.string.ImagesStatus), "2");
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
    }

    public void validateAllData(){
        validateIndividualData();
        validateBankData();
        validateSocialData();
        validateAgromicData();
        validateCommerceData();
        validatePartnerData();
        validateImageData();
    }
}
