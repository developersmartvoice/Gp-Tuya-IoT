package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thingclips.smart.android.user.api.ILoginCallback;
import com.thingclips.smart.android.user.bean.User;
import com.thingclips.smart.home.sdk.ThingHomeSdk;

public class UidLogin extends AppCompatActivity {

    private Button btnUidLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uid_login);

        btnUidLogin = findViewById(R.id.btnUidLogin);


        btnUidLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enables login with the UID.
                ThingHomeSdk.getUserInstance().loginOrRegisterWithUid(
                        "880",
                        "12345678910",
                        "123456",
                        new ILoginCallback() {
                    @Override
                    public void onSuccess(User user) {

                        Toast.makeText(UidLogin.this, "Logged in successfully. Username:" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UidLogin.this, Home.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String code, String error) {
                        Toast.makeText(UidLogin.this, "code: " + code + "error:" + error, Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}