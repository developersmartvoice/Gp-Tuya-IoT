package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.builder.ActivatorBuilder;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.api.IThingActivator;
import com.thingclips.smart.sdk.api.IThingActivatorGetToken;
import com.thingclips.smart.sdk.api.IThingSmartActivatorListener;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.enums.ActivatorEZStepCode;
import com.thingclips.smart.sdk.enums.ActivatorModelEnum;

import java.util.ArrayList;
import java.util.Arrays;

public class Home extends AppCompatActivity {

     private String homeName = "MyHome";

    private String [] rooms = {"Kitchen", "Bed Room", "Study"};
    private ArrayList<String> roomList;

    private HomeBean currentHomeBean;
    DeviceBean currentDeviceBean;

    IThingActivator thingActivator;


    private String ssId = "TP-Link_6179";
    private String pass = "01715001317";

    private Button btnSearch;
    private TextView dvName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        roomList = new ArrayList<>();
        roomList.addAll(Arrays.asList(rooms));

        createHome(homeName,roomList);

        btnSearch = findViewById(R.id.btnSearch);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText = btnSearch.getText().toString();

                if(thingActivator == null){
                    Toast.makeText(Home.this, "Wifi Config In Progress.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (btnText.equalsIgnoreCase("Search Device")){
                        thingActivator.start();
                        btnSearch.setText("Stop Search");
                    }
                    else {
                        thingActivator.stop();
                        btnSearch.setText("Search Device");
                    }
                }
            }
        });



    }

    private void createHome(String homeName,ArrayList<String>roomList) {
        ThingHomeSdk.getHomeManagerInstance().createHome(
                homeName,
                0,
                0,
                "",
                roomList,
                new IThingHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        currentHomeBean = bean;
                        Toast.makeText(Home.this, "Home Created Successfully.", Toast.LENGTH_SHORT).show();
                        getActivationToken();
                    }
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Toast.makeText(Home.this, "Home Creation Failed .", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getActivationToken(){

        long homeId = currentHomeBean.getHomeId();
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                new IThingActivatorGetToken() {

                    @Override
                    public void onSuccess(String token) {
                        Toast.makeText(Home.this, "Token ID Generated", Toast.LENGTH_SHORT).show();
                        serachDevice(token);
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Toast.makeText(Home.this, "Sorry Bro Not Happen.Token is not Generated and you got this "+s+" and this "+s1, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void serachDevice(String token){
        thingActivator = ThingHomeSdk.getActivatorInstance().newMultiActivator(new ActivatorBuilder()
                .setSsid(ssId)
                .setContext(this)
                .setPassword(pass)
                .setActivatorModel(ActivatorModelEnum.THING_EZ)
                .setTimeOut(1000)
                .setToken(token)
                .setListener(new IThingSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        Toast.makeText(Home.this, "Sorry Bro Not Happen you got this "+errorCode+" and this "+errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        Toast.makeText(Home.this, "Device Detection Successfully", Toast.LENGTH_SHORT).show();
                        currentDeviceBean = devResp;
                        Log.d("Shibam", "Device Id "+currentDeviceBean.devId+ "and Other" +currentDeviceBean);
                        Intent intent = new Intent(Home.this, DeviceControl.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onStep(String step, Object data) {
                        switch (step){
                            case ActivatorEZStepCode.DEVICE_BIND_SUCCESS:
                                Toast.makeText(Home.this, "Device bind Successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case ActivatorEZStepCode.DEVICE_FIND:
                                Toast.makeText(Home.this, "Device Found .", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }
}