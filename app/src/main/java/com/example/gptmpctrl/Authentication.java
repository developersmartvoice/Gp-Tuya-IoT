package com.example.gptmpctrl;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class Authentication extends AppCompatActivity {

//    private Button btnLogin;
//    private Button btnRegister;

    private EditText etNum;
//    private EditText etUidPass;
    private Button btnUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // Set the theme here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

//        btnLogin = findViewById(R.id.btnLogin);
//        btnRegister = findViewById(R.id.btnRegister);
        etNum = findViewById(R.id.etNum);
//        etUidPass = findViewById(R.id.etUidPass);
        btnUid = findViewById(R.id.btnUid);

//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the LoginActivity when the Login button is clicked
//                Intent intent = new Intent(Authentication.this, Login.class);
//                startActivity(intent);
//            }
//        });
//
//        btnRegister.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start the RegisterActivity when the Register button is clicked
//                Intent intent = new Intent(Authentication.this, Register.class);
//                startActivity(intent);
//            }
//        });

        btnUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uId = etNum.getText().toString();
//                String uIdPass = etUidPass.getText().toString();
                String uIdPass = "1234";

                if (uId.isEmpty()){
                    Toast.makeText(Authentication.this, "Enter Mobile Number for Login", Toast.LENGTH_SHORT).show();
                }
                else {
                    ThingHomeSdk.getUserInstance().loginOrRegisterWithUid(
                            "880",
                            uId,
                            uIdPass,
                            new ILoginCallback() {
                                @Override
                                public void onSuccess(User user) {

                                    // Display a dialog to get user selection
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Authentication.this);
                                    builder.setTitle("Pairing Selection");
                                    builder.setMessage("Do you want to pair automatically?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User selected automatic pairing
                                            Toast.makeText(Authentication.this, "Logged in successfully. Username:", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(Authentication.this, Home.class);
                                            startActivity(intent);
                                        }
                                    });

                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // User selected manual pairing
                                            Intent intent = new Intent(Authentication.this, ManualParing.class);
                                            startActivity(intent);
                                        }
                                    });


//                                    Toast.makeText(Authentication.this, "Logged in successfully. Username:" , Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(Authentication.this, Home.class);
//                                    startActivity(intent);
                                    builder.create().show();
                                }

                                @Override
                                public void onError(String code, String error) {
                                    Toast.makeText(Authentication.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}


