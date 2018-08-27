package com.example.nareshviriyala.farmifyagentfarmer.Helpers;

public class LogErrors {

    private static LogErrors logErrors;

    private LogErrors(){}  //private constructor

    public static LogErrors getInstance(){
        if (logErrors == null){ //if there is no instance available... create new one
            logErrors = new LogErrors();
        }
        return logErrors;
    }

    public void WriteLog(String className, String methodName, String error){
        String myclass = className;
        String mymethod = methodName;
        String myerror = error;
    }
}
