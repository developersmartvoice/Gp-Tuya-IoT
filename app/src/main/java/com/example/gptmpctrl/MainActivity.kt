package com.example.gptmpctrl

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.thingclips.smart.android.base.BaseConfig.THING_SMART_APPKEY
import com.thingclips.smart.android.base.BaseConfig.THING_SMART_SECRET
import com.thingclips.smart.home.sdk.ThingHomeSdk

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ThingHomeSdk
        ThingHomeSdk.setDebugMode(true)
        ThingHomeSdk.init(application, "p7esqgu5gut7gyng7hgm", "tjk4cq8dmhuxtw5xy53pxhuj7994dc8e")
        Log.d("Values", "Values are $THING_SMART_APPKEY and $THING_SMART_SECRET")
        Log.d("ThingSmartApp", "Successfully Initialized")

        // Start the Authentication activity
        val intent = Intent(this, Authentication::class.java)
        startActivity(intent)
    }
}

