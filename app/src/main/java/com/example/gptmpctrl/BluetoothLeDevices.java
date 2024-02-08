package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thingclips.smart.android.ble.api.BleScanResponse;
import com.thingclips.smart.android.ble.api.LeScanSetting;
import com.thingclips.smart.android.ble.api.ScanDeviceBean;
import com.thingclips.smart.android.ble.api.ScanType;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.ConfigProductInfoBean;
import com.thingclips.smart.sdk.api.IBleActivatorListener;
import com.thingclips.smart.sdk.api.IMultiModeActivatorListener;
import com.thingclips.smart.sdk.api.IThingDataCallback;
import com.thingclips.smart.sdk.bean.BleActivatorBean;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.MultiModeActivatorBean;
import com.thingclips.smart.sdk.bean.PauseStateData;

public class BluetoothLeDevices extends AppCompatActivity {
	
	private final String TAG = "BLUE_LE_DEVICES";
	private long homeId;
	private String token,msg;
	private TextView etBluLe;
	private Button btnStartLe,btnBleParing,btnCombo,btnComboOpti;
	private LeScanSetting scanSetting;
	private ScanDeviceBean beanG;
	private ConfigProductInfoBean resultG;
	private BleActivatorBean bleActivatorBean;
	private DeviceBean deviceBeanG;
	private MultiModeActivatorBean multiModeActivatorBean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_le_devices);
		etBluLe = findViewById(R.id.etBluLe);
		btnStartLe = findViewById(R.id.btnStartLe);
//		btnBleParing = findViewById(R.id.btnBleParing);
//		btnCombo=findViewById(R.id.btnCombo);
		btnComboOpti = findViewById(R.id.btnComboOpti);
		homeId = Long.parseLong(getIntent().getStringExtra("homeId"));
		token = getIntent().getStringExtra("token");
		Log.d(TAG, "Here is the HomeId : "+homeId);
		Log.d(TAG, "Here is the token ID: "+token);
		
		leScanSetting();
		
		btnStartLe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startLeScanning();
			}
		});
//		btnBleParing.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startParing();
//			}
//		});
//		btnCombo.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startComboParing();
//			}
//		});
		btnComboOpti.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startComboOptimizeParing();
			}
		});
	}
	
	public void leScanSetting(){
		scanSetting = new LeScanSetting.Builder()
				.setTimeout(60000) // The duration of the scanning. Unit: milliseconds.
				.addScanType(ScanType.SINGLE) // ScanType.SINGLE: scans for Bluetooth LE devices.
//				 .addScanType(ScanType.SIG_MESH) //ScanType.SIG_MESH: scans for other types of
				// devices.
				.build();
		Log.d(TAG, "leScanSetting: "+scanSetting.isNeedMatchUUID());
	}
	public void startLeScanning(){
		Log.d(TAG, "startLeScanning: Scanning Started!");
		Log.d(TAG, "startLeScanning: "+scanSetting.getUUID());
		etBluLe.setText("Scanning Started!..");
		// Starts scanning.
		ThingHomeSdk.getBleOperator().startLeScan(scanSetting, new BleScanResponse() {
			@Override
			public void onResult(ScanDeviceBean bean) {
				// The callback to return the scanning result.
				beanG = bean;
				Log.d(TAG, "onResult: Values from beans! "+beanG);
				Log.d(TAG, "onResult: Is bind? "+ beanG.getIsbind());
				Log.d(TAG, "onResult: Device type: "+beanG.getDeviceType());
//				etBluLe.setText("");
//				etBluLe.setText("");
				queryDeviceName();
//				ThingHomeSdk.getBleOperator().stopLeScan();
				 // The flag that
				// indicates whether a shared device is used.
				initNormal();
				initCombo();
//				btnBleParing.setVisibility(View.VISIBLE);
//				btnCombo.setVisibility(View.VISIBLE);
				btnComboOpti.setVisibility(View.VISIBLE);
			}
		});
	}
	public void queryDeviceName(){
		ThingHomeSdk.getActivatorInstance().getActivatorDeviceInfo(
				beanG.getProductId(),
				beanG.getUuid(),
				beanG.getMac(),
				new IThingDataCallback<ConfigProductInfoBean>() {
					@Override
					public void onSuccess(ConfigProductInfoBean result) {
						resultG = result;
						Log.d(TAG, "onSuccess: Device Info "+resultG.getName());
						etBluLe.setText("");
						etBluLe.setText(resultG.getName()+" Device Found!\n Is device paired? "+beanG.getIsbind());
					}
					
					@Override
					public void onError(String errorCode, String errorMessage) {
						Log.d(TAG, "onError: You got error on query device! "+errorMessage);
					}
				});
	}
	public void initNormal(){
		bleActivatorBean = new BleActivatorBean();
		// mScanDeviceBean: returned by ScanDeviceBean in the scanning result callback.
		bleActivatorBean.homeId = homeId; // homeId
		bleActivatorBean.address = beanG.getAddress(); // The device IP address.
		bleActivatorBean.deviceType = beanG.getDeviceType(); // The type of device.
		bleActivatorBean.uuid = beanG.getUuid(); // The UUID.
		bleActivatorBean.productId = beanG.getProductId(); // The product ID.
		bleActivatorBean.isShare = ((beanG.getFlag() >> 2) & 0x01) == 1;
	}
	public void initCombo(){
		multiModeActivatorBean = new MultiModeActivatorBean(beanG);

// mScanDeviceBean: returned by ScanDeviceBean in the scanning result callback.
		multiModeActivatorBean.deviceType = beanG.getDeviceType(); // The type of device.
		multiModeActivatorBean.uuid = beanG.getUuid(); // The UUID of the device.
		multiModeActivatorBean.address = beanG.getAddress(); // The IP address of the device.
		multiModeActivatorBean.mac = beanG.getMac(); // The MAC address of the device.
		multiModeActivatorBean.ssid = "Shibam123"; // The SSID of the target Wi-Fi network.
		multiModeActivatorBean.pwd = "Shibam123"; // The password of the target Wi-Fi network.
		multiModeActivatorBean.token = token; // The pairing token.
		multiModeActivatorBean.homeId = homeId; // The value of `homeId` for the current home.
		multiModeActivatorBean.timeout = 120000; // The timeout value.
	}
	public void startParing(){
		// Starts pairing.
		ThingHomeSdk.getActivator().newBleActivator().startActivator(bleActivatorBean, new IBleActivatorListener() {
			@Override
			public void onSuccess(DeviceBean deviceBean) {
				// The device is paired.
				deviceBeanG = deviceBean;
				Log.d(TAG, "onSuccess: Device Paired! "+deviceBeanG.devId);
				ThingHomeSdk.getBleManager().stopBleConfig(beanG.getUuid());
			}
			
			@Override
			public void onFailure(int code, String msg, Object handle) {
				// Failed to pair the device.
				Log.d(TAG, "onFailure: Not connected!");
			}
		});
	}
	public void startComboParing(){
		// Starts pairing.
		ThingHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
			@Override
			public void onSuccess(DeviceBean deviceBean) {
				// The device is paired.
				deviceBeanG = deviceBean;
				Log.d(TAG, "onSuccess: The device is paired!");
				Log.d(TAG, "onSuccess: Device Id is: "+deviceBeanG.devId);
				Log.d(TAG, "onSuccess: Device name is: "+deviceBeanG.getMac());
			}
			
			@Override
			public void onFailure(int code, String msg, Object handle) {
				// Failed to pair the device.
				Log.d(TAG,
						"onFailure: Combo paring failed! Msg is: "+msg+" and Objects are: "+handle.toString());
			}
		});
	}
	public void startComboOptimizeParing(){
		ThingHomeSdk.getActivator().newMultiModeActivator().startActivator(multiModeActivatorBean, new IMultiModeActivatorListener() {
			@Override
			public void onSuccess(DeviceBean deviceBean) {
				// The device is paired.
				deviceBeanG = deviceBean;
				Log.d(TAG, "onSuccess: Paring Successfully! ");
				Log.d(TAG, "onSuccess: Device Status: "+deviceBeanG.getConnectionStatus());
				ThingHomeSdk.getActivator().newMultiModeActivator().stopActivator(deviceBeanG.getUuid());
				etBluLe.setText("");
				etBluLe.setText("Device paired!");
				Intent intent = new Intent(BluetoothLeDevices.this,FourGangControl.class);
				intent.putExtra("homeId",String.valueOf(homeId));
				intent.putExtra("deviceId",deviceBeanG.devId);
				startActivity(intent);
			}
			
			@Override
			public void onFailure(int code, String msg, Object handle) {
				Log.d(TAG, "onFailure: Failed to paired with "+resultG.getName());
			}
			public void onActivatorStatePauseCallback(PauseStateData stateData) {
				// The data that is reported by the device, including the status of the connection to the router. For more information, see the descriptions of parameters.
				Log.d(TAG, "onActivatorStatePauseCallback: "+stateData.data);
				Log.d(TAG, "onActivatorStatePauseCallback: "+stateData.toString());
			}
		});
		
	}
}