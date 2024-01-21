package com.example.gptmpctrl;

import static android.graphics.Color.BLACK;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCCore;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
//import com.thingclips.smart.home.sdk.builder.ActivatorBuilder;
import com.thingclips.smart.home.sdk.builder.ThingCameraActivatorBuilder;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
//import com.thingclips.smart.sdk.api.IThingActivator;
import com.thingclips.smart.sdk.api.IThingActivatorGetToken;
import com.thingclips.smart.sdk.api.IThingCameraDevActivator;
import com.thingclips.smart.sdk.api.IThingSmartCameraActivatorListener;
import com.thingclips.smart.sdk.bean.DeviceBean;
//import com.tuya.smart.android.shortcutparser.api.IShortcutParserManager;
//import com.tuya.smart.android.shortcutparser.impl.ShortcutParserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.io.Serializable;

public class ManualParing extends AppCompatActivity {
//    IShortcutParserManager mIShortcutParserManager = new ShortcutParserManager();

    private String homeName = "MyHome"; // The name of a home.
    private String [] rooms = {"Kitchen", "Bed Room", "Study"};
    private ArrayList<String> roomList; // Roomlist for creating home
    private HomeBean currentHomeBean; // For storing Home Bean
    DeviceBean currentDeviceBean; //For storing device bean
    IThingCameraDevActivator mThingActivator; //Builder for searching devices
    private String url;
    private int widthAndHeight = 4;

    private EditText etWifiManual;
    private EditText etPassManual;
    private String ssId;
    private String pass;
    private String tokenGlobal; // For storing generated token

    private TextView etTxt1;
    private TextView etTxt2;
    private Button btnQrcode;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_paring);

        etWifiManual = findViewById(R.id.etWifiManual);
        etPassManual = findViewById(R.id.etPassManual);
        etTxt1 = findViewById(R.id.etHidView);
        etTxt2 = findViewById(R.id.etTokenView);
        btnQrcode = findViewById(R.id.btnQrCode);

        qrCodeImageView = findViewById(R.id.qrCodeView);

        roomList = new ArrayList<>();
        roomList.addAll(Arrays.asList(rooms));

        createHome(homeName,roomList); // CreateHome function is called

        btnQrcode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ssId = etWifiManual.getText().toString();
                pass = etPassManual.getText().toString();
                generateQrCode();

                mThingActivator.createQRCode();
                Log.d("QR  CODE", "create QR code called");
                if(mThingActivator == null){
                    Toast.makeText(ManualParing.this, "Wifi Config In Progress.", Toast.LENGTH_SHORT).show();
                }
                else {

                        if(ssId.isEmpty() && pass.isEmpty()) {
                            Toast.makeText(ManualParing.this,"Enter Wifi Name and Password Correctly", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // The builder will be activate for searching devices
                            mThingActivator.start();
                            Log.d("QR CODE","activator Started!");
//                            btnSearch.setText("Stop Search");
                        }
//

                }
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
                        Toast.makeText(ManualParing.this, "Home Created Successfully.", Toast.LENGTH_SHORT).show();
                        getActivationToken(); // This function is called for generating token
                    }
                    @Override
                    public void onError(String errorCode, String errorMsg) {
                        etTxt1.setText("Home Creation Failed.");
                        Toast.makeText(ManualParing.this, "Home Creation Failed .", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getActivationToken(){
        long homeId = currentHomeBean.getHomeId(); // Fetching homeId from created home for generating token
        // Generating token
        ThingHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                new IThingActivatorGetToken() {
                    @Override
                    public void onSuccess(String token) {
                        etTxt2.setText("Token ID Generated");
                        Toast.makeText(ManualParing.this, "Token ID Generated", Toast.LENGTH_SHORT).show();
//                        serachDevice(token);
                        tokenGlobal = token; // Storing the token for searching tuya devices
                    }
                    @Override
                    public void onFailure(String s, String s1) {
                        etTxt2.setText("Token ID is not Generated");
                        Toast.makeText(ManualParing.this, "Sorry Bro Not Happen.Token is not Generated and you got this "+s+" and this "+s1, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void generateQrCode(){
        mThingActivator = ThingHomeSdk.getActivatorInstance().newCameraDevActivator(new ThingCameraActivatorBuilder()
                .setContext(this)
                .setSsid(ssId)
                .setPassword(pass)
                .setToken(tokenGlobal)
                .setTimeOut(1000)
                .setListener(new IThingSmartCameraActivatorListener() {
                    @Override
                    public void onQRCodeSuccess(String qrcodeUrl) {
                        Log.d("QR CODE", "This is " +qrcodeUrl+ " and it means qr code success.");
                        try {
                            Log.d("QR CODE", "Try executed");
//                            createQRCode(qrcodeUrl,10);
                            Bitmap qrCodeBitmap = createQRCode(qrcodeUrl, 100);
                            qrCodeImageView.setImageBitmap(qrCodeBitmap);
                        } catch (WriterException e) {
                            throw new RuntimeException(e);
                        }

                    }

                    @Override
                    public void onError(String errorCode, String errorMsg) {

                        Log.d("QR CODE", errorMsg + " and "+ errorCode);

                    }

                    @Override
                    public void onActiveSuccess(DeviceBean devResp) {
                        currentDeviceBean = devResp;
                        Log.d("QR CODE", currentDeviceBean.toString());
                        Log.d("QR CODE", "onActiveSuccess: " +devResp.devId);
                        Log.d("QR CODE", "Device is Paired");
                        mThingActivator.stop();

                        IThingIPCCore cameraInstance = ThingIPCSdk.getCameraInstance();
                        if (cameraInstance != null) {
                            boolean isIt = cameraInstance.isIPCDevice(currentDeviceBean.devId);
                            if(isIt){
                                Log.d("QR CODE", "True");
                                int type = cameraInstance.getP2PType(currentDeviceBean.devId);
                                Log.d("QR CODE", "onActiveSuccess: Type is: "+type);
                                Intent intent = new Intent(ManualParing.this, GetCamera.class);
                                intent.putExtra("DeviceId", currentDeviceBean.devId); // Pass devResp as an extra with key "deviceBean"
                                startActivity(intent);
                            }else {
                                Log.d("QR CODE", "False");
                            }
                        }
                    }
                }));
    }
    public static Bitmap createQRCode(String url, int widthAndHeight)
            throws WriterException {
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN,0);
        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);

        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}