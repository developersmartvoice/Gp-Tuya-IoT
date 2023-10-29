package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class Login extends AppCompatActivity {

    private EditText etCountryCode, etLogEmail, etLogPass;
    private TextView textViewLogin;
    private Button btnLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCountryCode = findViewById(R.id.etCountryCode);
        etLogEmail = findViewById(R.id.etLogEmail);
        etLogPass = findViewById(R.id.etLogPass);
        textViewLogin = findViewById(R.id.textView2);
        btnLog = findViewById(R.id.btnLog);

        // Set the text of the TextView to "Login Form"
        textViewLogin.setText("Login Form");

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryCode = etCountryCode.getText().toString();
                String email = etLogEmail.getText().toString();
                String password = etLogPass.getText().toString();

                ThingHomeSdk.getUserInstance().loginWithEmail(
                        countryCode,
                        email,
                        password,
                        new ILoginCallback() {
                    @Override
                    public void onSuccess(User user) {
                        Toast.makeText(Login.this, "Logged in with Username: "+user, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(Login.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}