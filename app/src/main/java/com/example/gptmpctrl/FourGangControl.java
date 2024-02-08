package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.gson.Gson;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.bean.HomeBean;
import com.thingclips.smart.home.sdk.callback.IThingHomeResultCallback;
import com.thingclips.smart.sdk.api.IDevListener;
import com.thingclips.smart.sdk.api.IResultCallback;
import com.thingclips.smart.sdk.api.IThingDevice;
import com.thingclips.smart.sdk.bean.DeviceBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FourGangControl extends AppCompatActivity {
	private final String TAG = "4_GANG_CONTROL";
	private long homeId;
	private String deviceId,dpJson;
	private HomeBean homeBeanG;
	private List<DeviceBean> deviceList;
	private IThingDevice mDevice;
	private Map<String, Boolean> dp = new HashMap<>();
	private Switch switch1,switch2,switch3,switch4;
	
	Gson gson = new Gson();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_four_gang_control);
		homeId = Long.parseLong(getIntent().getStringExtra("homeId"));
		deviceId = getIntent().getStringExtra("deviceId");
		Log.d(TAG, "Home Id: "+homeId+" and device Id "+deviceId+" fetched!");
		if (homeId != 0 && !deviceId.isEmpty()){
			initView();
			getHomeDetail();
			buttonOperation();
		}
	}
	public void initView(){
		switch1 = findViewById(R.id.switch1);
		switch2 = findViewById(R.id.switch2);
		switch3 = findViewById(R.id.switch3);
		switch4 = findViewById(R.id.switch4);
	}
	public void getHomeDetail(){
		ThingHomeSdk.newHomeInstance(homeId).getHomeDetail(new IThingHomeResultCallback() {
			@Override
			public void onSuccess(HomeBean homeBean) {
				homeBeanG = homeBean;
				homeBeanG.getDeviceList().forEach(deviceBean -> {
					Log.d(TAG, "I want to see getDps keys!"+deviceBean.getDps());
					Log.d(TAG,
							"is support thing model device? : "+deviceBean.isSupportThingModelDevice());
					Log.d(TAG, "Access Type: "+deviceBean.getAccessType());
				});
				deviceList = homeBean.getDeviceList();
//				deviceList.get(0).getdp()
				Log.d(TAG, "onSuccess: Home bean initialized successfully! "+homeBeanG);
				Log.d(TAG, "onSuccess: Device List from HomeBean: "+deviceList);
				Log.d(TAG, "onSuccess: dev ID "+deviceList.get(0).devId);
				
				if(deviceList.get(0).devId != deviceId){
					Log.d(TAG, "onSuccess: Not Matched! try by bangla method!");
					mDevice = ThingHomeSdk.newDeviceInstance(deviceId);
					deviceListener();
				}
				else {
					Log.d(TAG, "onSuccess: Matched! hudai pera nilam!");
					mDevice = ThingHomeSdk.newDeviceInstance(deviceList.get(0).devId);
					deviceListener();
				}
			}
			@Override
			public void onError(String errorCode, String errorMsg) {
				Log.d(TAG, "onError: Getting errors on home detail calling method!! "+errorMsg);
			}
		});
	}
	public void deviceListener(){
		mDevice.registerDevListener(new IDevListener() {
			@Override
			public void onDpUpdate(String devId, String dpStr) {
				Log.d(TAG, "onDpUpdate: Testing. Dp Str: "+dpStr);
			}
			@Override
			public void onRemoved(String devId) {
				Log.d(TAG, "onRemoved: Device Removed!");
			}
			@Override
			public void onStatusChanged(String devId, boolean online) {
				Log.d(TAG, "onStatusChanged: Is device Online? "+online);
			}
			@Override
			public void onNetworkStatusChanged(String devId, boolean status) {
				Log.d(TAG, "onNetworkStatusChanged: What is the status? "+status);
			}
			@Override
			public void onDevInfoUpdate(String devId) {
				Log.d(TAG, "onDevInfoUpdate: "+devId);
			}
		});
	}
	public void buttonOperation(){
		switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					setDp("1",true);
					
				}
				else {
					setDp("1",false);
				}
			}
		});
		
		switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					setDp("2",true);
				}
				else {
					setDp("2",false);
				}
			}
		});
		
		switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					setDp("3",true);
				}
				else {
					setDp("3",false);
				}
			}
		});
		
		switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
					setDp("4",true);
				}
				else {
					setDp("4",false);
				}
			}
		});
	}
//	public void getDp(){
//		mDevice.getDp(, new IResultCallback() {
//			@Override
//			public void onError(String code, String error) {
//
//			}
//
//			@Override
//			public void onSuccess() {
//
//			}
//		});
//
//	}
	public void setDp(String code, boolean value){
//		dp.put("\""+code+"\"",value);
		dp.put(code,value);
		dpJson = gson.toJson(dp);
		Log.d(TAG, "setDp: is called and values are: "+dp);
		mDevice.publishDps(dpJson, new IResultCallback() {
			@Override
			public void onError(String code, String error) {
				// The error code 11001 is returned due to the following causes:
				// 1: Data has been sent in an incorrect format. For example, the data of String type has been sent in the format of Boolean data.
				// 2: Read-only DPs cannot be sent. For more information, see SchemaBean getMode. `ro` indicates the read-only type.
				// 3: Data of Raw type has been sent in a format rather than a hexadecimal string.
				Log.d(TAG, "onError: "+code);
				Log.d(TAG, "onError: The codes are: "+error);
			}
			@Override
			public void onSuccess() {
				Log.d(TAG, "onSuccess: Commend Send Successfully!");
			}
		});
	}
}
