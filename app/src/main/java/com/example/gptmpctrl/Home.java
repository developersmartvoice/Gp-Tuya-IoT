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

    private String homeName = "MyHome"; // The name of a home.
    private String [] rooms = {"Kitchen", "Bed Room", "Study"};
    private ArrayList<String> roomList; // Roomlist for creating home
    private HomeBean currentHomeBean; // For storing Home Bean
    DeviceBean currentDeviceBean; //For storing device bean
    IThingActivator thingActivator; //Builder for searching devices
    private long homeId;
    private EditText etWifiName;
    private EditText etWifiPass;
    private String ssId;
    private String pass;
    private Button btnSearch,btnBluetooth;
    private String tokenGlobal; // For storing generated token
    private TextView etTxt1;
    private TextView etTxt2;
    private TextView etTxt3;
    private TextView etTxt4;
    private TextView etTxt5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etWifiName = findViewById(R.id.etWifiName);
        etWifiPass = findViewById(R.id.etWifiPass);
        etTxt1 = findViewById(R.id.etTxt1);
        etTxt2 = findViewById(R.id.etTxt2);
        etTxt3 = findViewById(R.id.etTxt3);
        etTxt4 = findViewById(R.id.etTxt4);
        etTxt5 = findViewById(R.id.etTxt5);


        roomList = new ArrayList<>();
        roomList.addAll(Arrays.asList(rooms));

        createHome(homeName,roomList); // CreateHome function is called

        btnSearch = findViewById(R.id.btnSearch);
        btnBluetooth = findViewById(R.id.btnBluetooth);

        // Execute when search device button is clicked
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String btnText = btnSearch.getText().toString();
                ssId = etWifiName.getText().toString();
                pass = etWifiPass.getText().toString();
                serachDevice(ssId,pass,tokenGlobal); // Search device function is called with parameters
                if(thingActivator == null){
                    Toast.makeText(Home.this, "Wifi Config In Progress.", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (btnText.equalsIgnoreCase("Search Device")){
                        if(ssId.isEmpty() && pass.isEmpty()) {
                            Toast.makeText(Home.this,"Enter Wifi Name and Password Correctly", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // The builder will be activate for searching devices
                            thingActivator.start();
                            btnSearch.setText("Stop Search");
                        }
//                        thingActivator.start();
//                        btnSearch.setText("Stop Search to");
                    }
                    else {
                        // The builder will be deactivate
                        thingActivator.stop();
                        btnSearch.setText("Search Device");
                    }
                }
            }
        });
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String homeIdStr = homeId.
                Intent intent = new Intent(Home.this,BluetoothLeDevices.class);
                intent.putExtra("homeId",String.valueOf(homeId));
                intent.putExtra("token",tokenGlobal);
                startActivity(intent);
            }
        });
    }

    private void createHome(String homeName,ArrayList<String>roomList) {

        // Creating Home
        ThingHomeSdk.getHomeManagerInstance().createHome(
                homeName,
                0, // The longitude of a home
                0, // The latitude of a home
                "", //The geographical location of a home
                roomList, // The list of rooms
                new IThingHomeResultCallback() {
                    @Override
                    public void onSuccess(HomeBean bean) {
                        currentHomeBean = bean; // Bean is stored to currentHomeBean
                        etTxt1.setText("Home Created");
                        Toast.makeText(Home.this, "Home Created Successfully.", Toast.LENGTH_SHORT).show();
                        getActivationToken(); // This function is called for generating token
                    }
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        etTxt1.setText("Home Creation Failed.");
                        Toast.makeText(Home.this, "Home Creation Failed .", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getActivationToken(){
        homeId = currentHomeBean.getHomeId(); // Fetching homeId from created home for generating token
        // Generating token
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                new IThingActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        etTxt2.setText("Token ID Generated");
                        Toast.makeText(Home.this, "Token ID Generated", Toast.LENGTH_SHORT).show();
//                        serachDevice(token);
                        Log.d("home Token", "onSuccess: Token: "+token);
                        tokenGlobal = token; // Storing the token for searching tuya devices
                    }
                    @Override
                    public void onFailure(String s, String s1) {
                        etTxt2.setText("Token ID is not Generated");
                        Toast.makeText(Home.this, "Sorry Bro Not Happen.Token is not Generated and you got this "+s+" and this "+s1, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void serachDevice(String ssId, String pass,String tokenGlobal){
        thingActivator = ThingHomeSdk.getActivatorInstance().newMultiActivator(new ActivatorBuilder()
                .setSsid(ssId) // The name of the wifi network to which a paired device is connected
                .setContext(this) // The context to be set in activity
                .setPassword(pass) // The password of the wifi network to which a paired device is connected
                .setActivatorModel(ActivatorModelEnum.THING_EZ) //The pairing mode. For example, the value is set to ActivatorModelEnum.TY_EZ to represent the Wi-Fi EZ mode.
                .setTimeOut(1000) // The timeout value of a pairing task. Default value: 100. Unit: seconds.
                .setToken(tokenGlobal) // The pairing token.
                .setListener(new IThingSmartActivatorListener() {
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        etTxt3.setText("Sorry Bro Not Happen you got this "+errorCode+" and this "+errorMsg);
                        Toast.makeText(Home.this, "Sorry Bro Not Happen you got this "+errorCode+" and this "+errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        etTxt3.setText("Device Detection Successfully");
                        Toast.makeText(Home.this, "Device Detection Successfully", Toast.LENGTH_SHORT).show();
                        currentDeviceBean = devResp; // Responses
                        Log.d("Shibam", "Device Id "+currentDeviceBean.devId+ "and Other" +currentDeviceBean);
                        Intent intent = new Intent(Home.this, DeviceControl.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onStep(String step, Object data) {
                        switch (step){
                            case ActivatorEZStepCode.DEVICE_BIND_SUCCESS:
                                // This case will execute when device is finally bind with app
                                etTxt4.setText("Device bind Successfully");
                                Toast.makeText(Home.this, "Device bind Successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case ActivatorEZStepCode.DEVICE_FIND:
                                etTxt5.setText("Device Found.");
                                Toast.makeText(Home.this, "Device Found .", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }
}