package com.example.gptmpctrl;

import static com.thingclips.smart.android.base.BaseConfig.THING_SMART_APPKEY;
import static com.thingclips.smart.android.base.BaseConfig.THING_SMART_SECRET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.android.user.api.IRegisterCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.sdk.api.IResultCallback;

public class Register extends AppCompatActivity {

    private EditText etCtnCode, etVarCode, etEmail, etPass;
    private TextView etTitle;
    private Button btnVariCode, btnRegWithCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        ThingHomeSdk.init(getApplication(), "p7esqgu5gut7gyng7hgm", "tjk4cq8dmhuxtw5xy53pxhuj7994dc8e");
//        Log.d("Values", "Values are "+THING_SMART_APPKEY+ " and " +THING_SMART_SECRET);

        etCtnCode = findViewById(R.id.etCtnCode);
        etVarCode = findViewById(R.id.etVarCode);
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        etTitle = findViewById(R.id.etTitle);
        btnVariCode = findViewById(R.id.btnVariCode);
        btnRegWithCode = findViewById(R.id.btnRegWithCode);


        etTitle.setText("Registration Form");
        btnRegWithCode.setVisibility(View.INVISIBLE);
        etVarCode.setVisibility(View.INVISIBLE);

        btnVariCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the values from EditText views
                String countryCode = etCtnCode.getText().toString();
                String email = etEmail.getText().toString();


                // You can now use these values as needed
                // For example, you can display them or send them to a server

                ThingHomeSdk.getUserInstance().sendVerifyCodeWithUserName(email, "", countryCode, 1, new IResultCallback() {
                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(Register.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                        Log.d("Error", code+ "  " +error);
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(Register.this, "Verification code returned successfully.", Toast.LENGTH_SHORT).show();
                        btnRegWithCode.setVisibility(View.VISIBLE);
                        etVarCode.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        btnRegWithCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = etCtnCode.getText().toString();
                String verificationCode = etVarCode.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPass.getText().toString();
                ThingHomeSdk.getUserInstance().registerAccountWithEmail(
                        countryCode,
                        email,
                        password,
                        verificationCode,
                        new IRegisterCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Toast.makeText(Register.this, "Registered successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, Home.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(Register.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                        Log.d("Error", code+ "  " +error);
                    }
                });
            }
        });
    }
}