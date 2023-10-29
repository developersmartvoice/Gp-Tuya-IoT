package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DeviceControl extends AppCompatActivity {

    private TextView etDevCon;

    private String txt = "Device paired so You are here";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        etDevCon = findViewById(R.id.etDevCon);

        etDevCon.setText(txt);
    }
}