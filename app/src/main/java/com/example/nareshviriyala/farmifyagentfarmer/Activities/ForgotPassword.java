package com.example.nareshviriyala.farmifyagentfarmer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nareshviriyala.farmifyagentfarmer.Helpers.LogErrors;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;
import com.example.nareshviriyala.farmifyagentfarmer.Helpers.WebServiceOperation;
import com.example.nareshviriyala.farmifyagentfarmer.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    private TextInputLayout input_layout_email, input_layout_phone, input_layout_password, input_layout_confirm_password, input_layout_otp;
    private EditText input_email, input_phone, input_password, input_confirm_password, input_otp;
    private TextView tv_error;
    private Button btn_forgotpassword;
    public Validations validations;
    private ProgressBar pb_loading;
    public WebServiceOperation wso;
    public JSONObject forgotDataJson;
    public LogErrors logErrors;
    String className;
    public AlphaAnimation fadeIn, fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgotpassword);

        validations = new Validations();
        input_layout_email = (TextInputLayout)findViewById(R.id.input_layout_email);
        input_layout_phone = (TextInputLayout)findViewById(R.id.input_layout_phone);
        input_layout_password = (TextInputLayout)findViewById(R.id.input_layout_password);
        input_layout_confirm_password = (TextInputLayout)findViewById(R.id.input_layout_confirm_password);
        input_layout_otp = (TextInputLayout)findViewById(R.id.input_layout_otp);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tv_error = (TextView)findViewById(R.id.tv_error);

        input_email = (EditText)findViewById(R.id.input_email);
        input_phone = (EditText)findViewById(R.id.input_phone);
        input_password = (EditText)findViewById(R.id.input_password);
        input_confirm_password = (EditText)findViewById(R.id.input_confirm_password);
        input_otp = (EditText)findViewById(R.id.input_otp);

        btn_forgotpassword = (Button)findViewById(R.id.btn_forgotpassword);
        fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

        wso = new WebServiceOperation();
        logErrors = LogErrors.getInstance(this);
        forgotDataJson = new JSONObject();
        className = new Object(){}.getClass().getEnclosingClass().getName();

        btn_forgotpassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    if(forgotDataJson.has("Phone")) { //this means that user data is already sent now, OTP is being sent
                        if (!validatePassword())
                            return;
                        if (!validateConfirmPassword())
                            return;
                        if (!validateOTP())
                            return;
                        forgotDataJson.put("OTP", input_otp.getText().toString().trim());
                        forgotDataJson.put("Password", input_password.getText().toString().trim());
                        new callResetPasswordAPI().execute();
                    }else{
                        if (!validateEmail())
                            return;
                        if (!validatePhone())
                            return;

                        forgotDataJson.put("Email", input_email.getText().toString().trim());
                        forgotDataJson.put("Phone", input_phone.getText().toString().trim());
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        new callValidatePhoneEmailAPI().execute();
                    }
                }catch (JSONException e) {
                    logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
                }catch (Exception ex){
                    logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
                }
            }
        });
    }

    public class callValidatePhoneEmailAPI extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_forgotpassword.setText("");
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject response = new JSONObject();
            try {
                response = wso.MakePostCall("Users/forgotpassword", forgotDataJson.toString(), null);
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {

                if(result.getInt("responseCode") == 200) {
                    showOTPControl();
                    pb_loading.setVisibility(View.INVISIBLE);
                    btn_forgotpassword.setText("Verify OTP");
                }
                else {
                    forgotDataJson.remove("Phone");
                    forgotDataJson.remove("Email");
                    tv_error.setText(result.getString("response"));
                    tv_error.startAnimation(fadeIn);
                    tv_error.startAnimation(fadeOut);
                    fadeIn.setDuration(1200);
                    fadeIn.setFillAfter(true);
                    fadeOut.setDuration(1200);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(2200+fadeIn.getStartOffset());
                    pb_loading.setVisibility(View.INVISIBLE);
                    btn_forgotpassword.setText("Send OTP");
                }
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }
    }


    public class callResetPasswordAPI extends AsyncTask<Object, Void, JSONObject> {
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_forgotpassword.setText("");
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject response = new JSONObject();
            try {
                response = wso.MakePostCall("Users/updatepassword", forgotDataJson.toString(), null);
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            try {
                pb_loading.setVisibility(View.INVISIBLE);
                btn_forgotpassword.setText("Send OTP");
                if(result.getInt("responseCode") == 200) {
                    tv_error.setText(result.getString("response"));
                    tv_error.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv_error.startAnimation(fadeIn);
                    tv_error.startAnimation(fadeOut);
                    fadeIn.setDuration(1200);
                    fadeIn.setFillAfter(true);
                    fadeOut.setDuration(1200);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(2200+fadeIn.getStartOffset());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            goToSignInPage(null);
                        }
                    }, 2200);

                }
                else {
                    tv_error.setText(result.getString("response"));
                    tv_error.startAnimation(fadeIn);
                    tv_error.startAnimation(fadeOut);
                    fadeIn.setDuration(1200);
                    fadeIn.setFillAfter(true);
                    fadeOut.setDuration(1200);
                    fadeOut.setFillAfter(true);
                    fadeOut.setStartOffset(2200+fadeIn.getStartOffset());
                }
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
        }
    }

    public void showOTPControl(){
        try{
            input_layout_otp.setVisibility(View.VISIBLE);
            input_layout_password.setVisibility(View.VISIBLE);
            input_layout_confirm_password.setVisibility(View.VISIBLE);
            btn_forgotpassword.setText("Verify OTP");
        }catch (Exception ex){
            logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
        }
    }

    public void goToSignInPage(View view){
        Intent Intent = new Intent(this, BootActivity.class);
        startActivity(Intent);
        this.overridePendingTransition(R.anim.slideinleft,R.anim.slideoutleft);
        this.finish();
    }

    private void requestFocus(View view) {
        if (view.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private boolean validateEmail() {
        String val = validations.validateEmail(input_email.getText().toString().trim());
        if (val != null) {
            input_layout_email.setError(val);
            requestFocus(input_email);
            return false;
        }else {
            input_layout_email.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        String val = validations.validatePhone(input_phone.getText().toString().trim());
        if (val != null) {
            input_layout_phone.setError(val);
            requestFocus(input_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassword() {
        String val = validations.validatePassword(input_password.getText().toString().trim());
        if (val != null) {
            input_layout_password.setError(val);
            requestFocus(input_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateConfirmPassword() {
        String val = validations.validateConfirmPassword(input_confirm_password.getText().toString().trim(), input_password.getText().toString().trim());
        if (val != null) {
            input_layout_confirm_password.setError(val);
            requestFocus(input_confirm_password);
            return false;
        } else {
            input_layout_confirm_password.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateOTP() {
        String val = validations.validateOTP(input_otp.getText().toString().trim());
        if (val != null) {
            input_layout_otp.setError(val);
            requestFocus(input_otp);
            return false;
        } else {
            input_layout_otp.setErrorEnabled(false);
        }
        return true;
    }
}
