package com.example.gptmpctrl

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.thingclips.loguploader.TLogSDK
import com.thingclips.sdk.security.SecuredPreferenceStore
import com.thingclips.smart.home.sdk.ThingHomeSdk


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ThingHomeSdk
        ThingHomeSdk.setDebugMode(true)
        ThingHomeSdk.init(application, "p7esqgu5gut7gyng7hgm", "tjk4cq8dmhuxtw5xy53pxhuj7994dc8e")
//        FrescoManager.initFresco(application);
        Fresco.initialize(applicationContext)


//        Log.d("Values", "Values are $THING_SMART_APPKEY and $THING_SMART_SECRET")
        Log.d("ThingSmartApp", "Successfully Initialized")

        /*
         * 表示单个日志文件最大10M,同种类日志文件数最大为5个
         * 当日志数量达到最大时，每创建一个新日志文件，时间上最早创建的日志文件会被删除
         */TLogSDK.init(application)
//        SecuredPreferenceStore.init(applicationContext)

        // Start the Authentication activity
        val intent = Intent(this, Authentication::class.java)
//        SecuredPreferenceStore.init(applicationContext);
        startActivity(intent)
    }
}

