package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thingclips.smart.android.blemesh.api.IThingBlueMeshSearch;
import com.thingclips.smart.android.blemesh.api.IThingBlueMeshSearchListener;
import com.thingclips.smart.android.blemesh.bean.SearchDeviceBean;
import com.thingclips.smart.android.blemesh.builder.SearchBuilder;
import com.thingclips.smart.home.sdk.ThingHomeSdk;
import com.thingclips.smart.home.sdk.api.IThingHome;
import com.thingclips.smart.home.sdk.bean.ConfigProductInfoBean;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;
import com.thingclips.smart.sdk.api.IThingDataCallback;
import com.thingclips.smart.sdk.bean.DeviceBean;
import com.thingclips.smart.sdk.bean.SigMeshBean;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class BluetoothMode extends AppCompatActivity {
	private final String TAG = "BLU_MD";
	private long homeId;
	private TextView etBlV1;
	private Button btnBtScan;
	private SigMeshBean sigMeshBeanG;
	private IThingHome mThingHome;
	private List<SigMeshBean> meshList;
	private List<DeviceBean> meshSubDevList;
	private IThingBlueMeshSearchListener iThingBlueMeshSearchListener;
	private IThingBlueMeshSearch mMeshSearch;
	private SearchBuilder searchBuilder;
	private SearchDeviceBean deviceBeanG;
	private ConfigProductInfoBean resultG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_mode);
		btnBtScan = findViewById(R.id.btnBtScan);
		etBlV1 = findViewById(R.id.etBlV1);
		homeId = Long.parseLong(getIntent().getStringExtra("homeId"));
		Log.d(TAG, "Here is the HomeId : "+homeId);
		
		//Creating a Bluetooth Mesh Network
		createBluetoothMesh();
		
		mThingHome = ThingHomeSdk.newHomeInstance(homeId); // The `homeId` parameter.
		
		if (mThingHome.getHomeBean() != null){
			meshList = ThingHomeSdk.getSigMeshInstance().getSigMeshList();
			meshSubDevList =
					ThingHomeSdk.newSigMeshDeviceInstance(sigMeshBeanG.getMeshId()).getMeshSubDevList();
			
		}
		ThingHomeSdk.getThingSigMeshClient().initMesh(sigMeshBeanG.getMeshId());
		// Starts a connection.
		ThingHomeSdk.getThingSigMeshClient().startClient(sigMeshBeanG);
		// Starts a connection with a specified scanning period.
//		ThingHomeSdk.getThingSigMeshClient().startClient(mSigMeshBean,searchTime);

		// Closes a connection.
		ThingHomeSdk.getThingSigMeshClient().stopClient();
		
		
		iThingBlueMeshSearchListener =new IThingBlueMeshSearchListener() {
			@Override
			public void onSearched(SearchDeviceBean deviceBean) {
				deviceBeanG = deviceBean;
				etBlV1.setText("Searching .. "+deviceBeanG.scanRecord);
				Log.d(TAG, "onSearched: "+deviceBeanG.device);
				Log.d(TAG, "onSearched: Name: "+deviceBeanG.meshName);
				
			}
			
			@Override
			public void onSearchFinish() {
				Log.d(TAG, "onSearchFinish: Saying It's Finished!");
				etBlV1.setText("Finished!");
				queryDeviceInfo();
			}
		};
		// The UUID of a Bluetooth mesh device to be paired is unchanged.
		UUID[] MESH_PROVISIONING_UUID = {UUID.fromString("00001827-0000-1000-8000-00805f9b34fb")};
		searchBuilder = new SearchBuilder()
				.setServiceUUIDs(MESH_PROVISIONING_UUID)    // The UUID of the Bluetooth mesh device is a fixed value.
				.setTimeOut(100)        // The duration of the scanning. Unit: seconds.
				.setThingBlueMeshSearchListener(iThingBlueMeshSearchListener).build();
		
		mMeshSearch = ThingHomeSdk.getThingBlueMeshConfig().newThingBlueMeshSearch(searchBuilder);
		
		btnBtScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(btnBtScan.getText().toString().toUpperCase() == "SCAN"){
					// Starts scanning.
					mMeshSearch.startSearch();
					btnBtScan.setText("STOP SCAN");
				}
				else {
					// Stops scanning.
					mMeshSearch.stopSearch();
					btnBtScan.setText("SCAN");
				}
			}
		});
	}
	
	public void createBluetoothMesh(){
		ThingHomeSdk.newHomeInstance(homeId) // The `homeId` parameter of long type.
				.createSigMesh(new IThingResultCallback<SigMeshBean>() {
					
					@Override
					public void onError(String errorCode, String errorMsg) {
					
					}
					
					@Override
					public void onSuccess(SigMeshBean sigMeshBean) {
						sigMeshBeanG = sigMeshBean;
						Log.d(TAG, "onSuccess: Mesh Whole Info "+sigMeshBeanG);
						Log.d(TAG, "onSuccess: Mesh ID: "+sigMeshBeanG.getMeshId());
					}
				});
		
	}
	public void queryDeviceInfo(){
		ThingHomeSdk.getActivatorInstance().getActivatorDeviceInfo(
				// btye[] to String
				new String(deviceBeanG.getProductId(), StandardCharsets.UTF_8),
				// uuid
				null,
				// mac
				deviceBeanG.getMacAdress(),
				// callback
				new IThingDataCallback<ConfigProductInfoBean>() {
					@Override
					public void onSuccess(ConfigProductInfoBean result) {
						resultG = result;
						Log.d(TAG, "onSuccess: Name is "+resultG.getName());
					}
					
					@Override
					public void onError(String errorCode, String errorMessage) {
					
					}
				});
		
	}
}