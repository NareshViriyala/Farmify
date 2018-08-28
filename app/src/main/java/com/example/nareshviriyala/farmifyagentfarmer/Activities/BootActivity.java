package com.example.nareshviriyala.farmifyagentfarmer.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class BootActivity extends AppCompatActivity {

    private EditText input_phone, input_password;
    private Button btn_signin;
    private TextInputLayout input_layout_phone, input_layout_password;
    private TextView tv_error;
    private ProgressBar pb_loading;
    public Validations validations;
    public WebServiceOperation wso;
    public LogErrors logErrors;
    public String className;
    public AlphaAnimation fadeIn, fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_boot);

        logErrors = LogErrors.getInstance();
        validations = new Validations();
        wso = new WebServiceOperation();
        input_phone = (EditText)findViewById(R.id.input_phone);
        input_password = (EditText)findViewById(R.id.input_password);
        btn_signin = (Button)findViewById(R.id.btn_signin);
        input_layout_phone = (TextInputLayout)findViewById(R.id.input_layout_phone);
        input_layout_password = (TextInputLayout)findViewById(R.id.input_layout_password);
        tv_error = (TextView)findViewById(R.id.tv_error);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

        fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        className = new Object(){}.getClass().getEnclosingClass().getName();
        //lnk_register = (TextView)findViewById(R.id.lnk_register);

        //input_phone.addTextChangedListener(new MyTextWatcher(input_phone));
        //input_password.addTextChangedListener(new MyTextWatcher(input_password));

        btn_signin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!validatePhone())
                    return;
                if (!validatePassword())
                    return;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                new callLoginAPI().execute(input_phone.getText().toString().trim(), input_password.getText().toString().trim());
                //Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                //goToMainPage();
            }
        });

    }

    public class callLoginAPI extends AsyncTask<String, Void, JSONObject>{
        @Override
        protected void onPreExecute(){
            pb_loading.setVisibility(View.VISIBLE);
            btn_signin.setText("");
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject response = new JSONObject();
            try {
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Phone", strings[0]);
                postDataParams.put("Password", strings[1]);
                response = wso.MakePostCall("Users/authenticate", postDataParams.toString());
            }catch (JSONException e) {
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), e.getMessage().toString());
            }catch (Exception ex){
                logErrors.WriteLog(className, new Object(){}.getClass().getEnclosingMethod().getName(), ex.getMessage().toString());
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                pb_loading.setVisibility(View.INVISIBLE);
                btn_signin.setText("Sign In");
                if(result.getInt("responseCode") == 200) {
                    goToMainPage();
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

    public void goToMainPage()
    {
        Intent Intent = new Intent(this, MainActivity.class);
        startActivity(Intent);
        this.overridePendingTransition(R.anim.slideinleft,R.anim.slideoutleft);
        this.finish();
    }

    public void goToRegisterPage(View view){
        Intent Intent = new Intent(this, RegisterActivity.class);
        startActivity(Intent);
        this.overridePendingTransition(R.anim.slideinright,R.anim.slideoutright);
        this.finish();
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

    private void requestFocus(View view) {
        if (view.requestFocus())
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        if(tv_error.getVisibility() == View.VISIBLE) {
            tv_error.setVisibility(View.INVISIBLE);;
        }
    }

    /*
    private class MyTextWatcher implements TextWatcher{
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){}

        public void afterTextChanged(Editable editable){
            switch (view.getId()) {
                case R.id.input_phone:
                    validatePhone();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }
    */
}
