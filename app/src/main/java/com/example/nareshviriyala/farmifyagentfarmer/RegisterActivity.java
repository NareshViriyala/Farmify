package com.example.nareshviriyala.farmifyagentfarmer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nareshviriyala.farmifyagentfarmer.Helpers.Validations;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout input_layout_first_name, input_layout_last_name, input_layout_email, input_layout_phone, input_layout_password, input_layout_confirm_password;
    private EditText input_first_name, input_last_name, input_email, input_phone, input_password, input_confirm_password;
    private Button btn_register;
    public Validations validations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        validations = new Validations();
        input_layout_first_name = (TextInputLayout)findViewById(R.id.input_layout_first_name);
        input_layout_last_name = (TextInputLayout)findViewById(R.id.input_layout_last_name);
        input_layout_email = (TextInputLayout)findViewById(R.id.input_layout_email);
        input_layout_phone = (TextInputLayout)findViewById(R.id.input_layout_phone);
        input_layout_password = (TextInputLayout)findViewById(R.id.input_layout_password);
        input_layout_confirm_password = (TextInputLayout)findViewById(R.id.input_layout_confirm_password);

        input_first_name = (EditText)findViewById(R.id.input_first_name);
        input_last_name = (EditText)findViewById(R.id.input_last_name);
        input_email = (EditText)findViewById(R.id.input_email);
        input_phone = (EditText)findViewById(R.id.input_phone);
        input_password = (EditText)findViewById(R.id.input_password);
        input_confirm_password = (EditText)findViewById(R.id.input_confirm_password);

        btn_register = (Button)findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!validateFirstName())
                    return;
                if (!validateLastName())
                    return;
                if (!validateEmail())
                    return;
                if (!validatePhone())
                    return;
                if (!validatePassword())
                    return;
                if (!validateConfirmPassword())
                    return;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
            }
        });

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

    private boolean validateFirstName() {
        String val = validations.validateName(input_first_name.getText().toString().trim());
        if (val != null) {
            input_layout_first_name.setError(val);
            requestFocus(input_first_name);
            return false;
        } else {
            input_layout_first_name.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastName() {
        String val = validations.validateName(input_last_name.getText().toString().trim());
        if (val != null) {
            input_layout_last_name.setError(val);
            requestFocus(input_last_name);
            return false;
        } else {
            input_layout_last_name.setErrorEnabled(false);
        }
        return true;
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
}
