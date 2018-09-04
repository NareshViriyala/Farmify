package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

import java.util.regex.Pattern;

public class Validations {
    public String validateName(String value) {
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide name";
        }
        return returnVal;
    }

    public String validateEmail(String value) {
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide email";
        } else if(!Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(value.trim()).matches()){
            returnVal = "Not a valid email address";
        }
        return returnVal;
    }

    public String validatePhone(String value) {
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide phone number";
        } else if (value.trim().length() < 10){
            returnVal = "Invalid phone number";
        } else if(!Pattern.compile("^[6-9][0-9]{9}$").matcher(value.trim()).matches()){
            returnVal = "Not a valid phone number";
        }
        return returnVal;
    }

    public String validatePassword(String value) {
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide password";
        } else if (value.trim().length() < 6){
            returnVal = "Invalid password length";
        }
        return returnVal;
    }

    public String validateConfirmPassword(String value1, String value2) {
        String returnVal = null;
        if (!value1.trim().equals(value2.trim())) {
            returnVal = "Passwords do not match";
        }
        return returnVal;
    }

    public String validateOTP(String value){
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide OTP";
        } else if (!value.matches("\\d+(?:\\.\\d+)?")){
            returnVal = "Invalid OTP";
        }
        return returnVal;
    }

    public String validateAadhar(String value){
        String returnVal = null;
        if (value.trim().isEmpty()) {
            returnVal = "Please provide aadhar number";
        } else if (value.trim().replace(" ","").length() != 12){
            returnVal = "Invalid aadhar number";
        }
        return returnVal;
    }
}
