package com.example.gptmpctrl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.thingclips.imagepipeline.okhttp3.DecryptImageRequest;
import com.thingclips.smart.android.camera.sdk.ThingIPCSdk;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCCloud;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCCore;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCDoorbell;
import com.thingclips.smart.android.camera.sdk.api.IThingIPCDpHelper;
import com.thingclips.smart.android.camera.sdk.bean.CloudStatusBean;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.AbsP2pCameraListener;
import com.thingclips.smart.camera.camerasdk.thingplayer.callback.OperationDelegateCallBack;
import com.thingclips.smart.camera.ipccamerasdk.cloud.IThingCloudCamera;
import com.thingclips.smart.camera.middleware.cloud.bean.TimePieceBean;
import com.thingclips.smart.camera.middleware.p2p.IThingSmartCameraP2P;
import com.thingclips.smart.camera.middleware.widget.AbsVideoViewCallback;
import com.thingclips.smart.camera.middleware.widget.ThingCameraView;
import com.thingclips.smart.home.sdk.callback.IThingResultCallback;

import java.util.ArrayList;
import java.util.List;

public class GetCamera extends AppCompatActivity {

    private String TAG = "CAMERA_ACTIVITY";
//    private DeviceBean currentDeviceBean;
    private String deviceId;

    private boolean isLiveStreaming = false;

    private boolean isSupportCloudStorage;

    private String yearMonthDay = "2024-01-21";

    private int year;
    private int month;
    private int day;

    String[] substring;

    private List<TimePieceBean> timeList = new ArrayList<>();

    private IThingCloudCamera cloudCamera;

    IThingIPCDpHelper ipcDpHelper;

    private static final int ASPECT_RATIO_WIDTH = 9;
    private static final int ASPECT_RATIO_HEIGHT = 16;

    // 1. Create `IThingSmartCameraP2P`.
    IThingSmartCameraP2P mCameraP2P = null;
    IThingIPCCore cameraInstance = ThingIPCSdk.getCameraInstance();

    SimpleDraweeView img;
    ThingCameraView mVideoView;

    AbsP2pCameraListener absP2pCameraListener;

    private Button btnStopPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_camera);


        substring = yearMonthDay.split("-");


        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = width * ASPECT_RATIO_WIDTH / ASPECT_RATIO_HEIGHT;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);

        findViewById(R.id.camera_video_view_Rl).setLayoutParams(layoutParams);




        Intent intent = getIntent();

        if (intent.hasExtra("DeviceId")){
            deviceId = intent.getStringExtra("DeviceId");
            Log.d(TAG, "Device ID: "+deviceId);

            IThingIPCCloud cloud = ThingIPCSdk.getCloud();
            if (cloud != null) {
                isSupportCloudStorage = cloud.isSupportCloudStorage(deviceId);
                if(isSupportCloudStorage){
                    cloudCamera = cloud.createCloudCamera();

                    cloudCamera.queryCloudServiceStatus(deviceId, new IThingResultCallback<CloudStatusBean>() {
                        @Override
                        public void onSuccess(CloudStatusBean result) {
                            //Get cloud storage status
                            Log.d(TAG, "onSuccess: "+result);
                        }

                        @Override
                        public void onError(String errorCode, String errorMessage) {

                        }
                    });

                }

            }



            btnStopPreview = findViewById(R.id.btnStopPreview);


            if (cameraInstance != null) {
                mCameraP2P = cameraInstance.createCameraP2P(deviceId);
                Log.d(TAG, "Camera instance executed");
            }


            img = findViewById(R.id.img);
            Log.d(TAG, "SimpleDraweeView is initialized!");
            //SimpleDraweeView is initialized!

            mVideoView = findViewById(R.id.camera_video_view);
            Log.d(TAG, "mVideoView initialized");


            // 2. Set the callback for the view rendering container.
            mVideoView.setViewCallback(new AbsVideoViewCallback() {
                @Override
                public void onCreated(Object view) {
                    super.onCreated(view);

                    // 3. Bind the rendered view with `IThingSmartCameraP2P`.
                    if (null != mCameraP2P){
                        IThingIPCDoorbell doorbell = ThingIPCSdk.getDoorbell();
                        if (doorbell != null) {
                            doorbell.wirelessWake(deviceId);
                            Log.d(TAG, doorbell.getIPCDoorBellManagerInstance().toString());
                        }
                        mCameraP2P.generateCameraView(view);
//                        mVideoView.createdView();
                        Log.d(TAG, "CameraView Generated");
                    }
                }
            });



            try {
                mVideoView.createVideoView(deviceId);
//                mVideoView.createdView();
                Log.d(TAG, "CreatedVideoView is not giving errors!!");

            }catch (Exception e){
                Log.d(TAG, "Why this Error!! "+e);
//                Log.d(TAG,  e.toString());
            }



            // 5. Register a P2P listener.
            absP2pCameraListener = new AbsP2pCameraListener() {
                @Override
                public void onSessionStatusChanged(Object camera, int sessionId, int sessionStatus) {
                    super.onSessionStatusChanged(camera, sessionId, sessionStatus);

                    // If sessionStatus = -3 (timeout) or  -105 (failed authentication), we recommend that you initiate a reconnection. Make sure to avoid an infinite loop.
                }
            };
            if (null != mCameraP2P){
                mCameraP2P.registerP2PCameraListener(absP2pCameraListener);

//                mCameraP2P.generateCameraView(mVideoView.createdView());

                Log.d(TAG, "Registered Successfully");



                mCameraP2P.connect(deviceId,1, new OperationDelegateCallBack() {
                    @Override
                    public void onSuccess(int sessionId, int requestId, String data) {
                        // A P2P connection is created.
                        Log.d(TAG, "P2P connection is created");
                        setLiveStreaming();

                    }

                    @Override
                    public void onFailure(int sessionId, int requestId, int errCode) {
                        // Failed to create a P2P connection.
                        Log.d(TAG, "P2P connection failed");
                    }
                });

            }

        }
        else {
            Log.d(TAG, "Device Id is not Found!");
        }

        if(isLiveStreaming){
            btnStopPreview.setVisibility(View.VISIBLE);
        }

        btnStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsetLiveStreaming();
            }
        });
    }

//    public void stopLiveStream(){
//
//
//    }
//    public void simpleDraweeViewInit(){
//
//        if (mUriString.contains("@")) {
//            int index = mUriString.lastIndexOf("@");
//            try {
//                String decryption = mUriString.substring(index + 1);
//                String imageUrl = mUriString.substring(0, index);
//                ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
//                        .setRotationOptions(RotationOptions.autoRotateAtRenderTime())
//                        .disableDiskCache();
//                DecryptImageRequest imageRequest = new DecryptImageRequest(builder, "AES", "AES/CBC/PKCS5Padding",
//                        decryption.getBytes());
//                DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(imageRequest)
//                        .build();
//                img.setController(controller);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            Uri uri = null;
//            try {
//                uri = Uri.parse(mUriString);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            DraweeController controller = Fresco.newDraweeControllerBuilder().setUri(uri)
//                    .build();
//            img.setController(controller);
//        }
//
//    }

    public void setLiveStreaming(){
        mCameraP2P.startPreview(new OperationDelegateCallBack() {
            @Override
            public void onSuccess(int sessionId, int requestId, String data) {
                // Live video streaming is started.
                Log.d(TAG, "Live video streaming is started");
                isLiveStreaming = true;
            }

            @Override
            public void onFailure(int sessionId, int requestId, int errCode) {
                // Failed to start live video streaming.
                Log.d(TAG, "Failed to start");
            }
        });
    }
    public void unsetLiveStreaming(){
        mCameraP2P.stopPreview(new OperationDelegateCallBack() {
            @Override
            public void onSuccess(int sessionId, int requestId, String data) {
                Log.d(TAG, "Preview Stopped");
                mCameraP2P.removeOnP2PCameraListener(absP2pCameraListener);
                mCameraP2P.destroyP2P();
                isLiveStreaming = false;
            }

            @Override
            public void onFailure(int sessionId, int requestId, int errCode) {

            }
        });
    }

//    public void getAllVideoClipsStoredOnDay(){
//        year = Integer.parseInt(substring[0]);
//        month = Integer.parseInt(substring[1]);
//        day = Integer.parseInt(substring[2]);
//        mCameraP2P.queryRecordTimeSliceByDay(year, month, day, new OperationDelegateCallBack() {
//            @Override
//            public void onSuccess(int sessionId, int requestId, String data) {
//                // `data` indicates the list of returned video clips for the specified date.
//                parsePlaybackData(data);
//            }
//
//            @Override
//            public void onFailure(int sessionId, int requestId, int errCode) {
////                mHandler.sendEmptyMessage(MSG_DATA_DATE_BY_DAY_FAIL);
//            }
//        });
//    }
//    public class RecordInfoBean {
//        private int count;
//        private List<TimePieceBean> items;
//
//        public int getCount() {
//            return count;
//        }
//
//        public void setCount(int count) {
//            this.count = count;
//        }
//
//        public List<TimePieceBean> getItems() {
//            return items;
//        }
//
//        public void setItems(List<TimePieceBean> items) {
//            this.items = items;
//        }
//    }
//
//    private void parsePlaybackData(Object obj) {
//
//        RecordInfoBean recordInfoBean = JSONObject.parseObject(obj.toString(), RecordInfoBean.class);
//        timeList.clear();
//        if (recordInfoBean.getCount() != 0) {
//            List<TimePieceBean> timePieceBeanList = recordInfoBean.getItems();
//            if (timePieceBeanList != null && timePieceBeanList.size() != 0) {
//                timeList.addAll(timePieceBeanList);
//            }
////            mHandler.sendMessage(MessageUtil.getMessage(MSG_DATA_DATE_BY_DAY_SUCC, ARG1_OPERATE_SUCCESS));
//        } else {
////            mHandler.sendMessage(MessageUtil.getMessage(MSG_DATA_DATE_BY_DAY_FAIL, ARG1_OPERATE_FAIL));
//        }
//    }
}

