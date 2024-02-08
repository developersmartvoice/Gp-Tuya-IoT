plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.gptmpctrl"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.gptmpctrl"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }
    }

    signingConfigs {
        create("configName") {
            storeFile = file("D:\\Android Studio Projects\\GpTempCtrl\\GpTempCtrl\\app\\libs\\gptmpctrl.jks")
//            D:\Android Studio Projects\GpTempCtrl\GpTempCtrl\app
            storePassword = "a643637"
            keyAlias = "shibam"
            keyPassword = "a643637"
        }
    }


    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("configName")
            // ... other debug configurations
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("configName")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            jniLibs.pickFirsts.add("lib/*/libc++_shared.so")
            jniLibs.pickFirsts.add("lib/*/libyuv.so")
            jniLibs.pickFirsts.add("lib/*/libopenh264.so")
        }
    }
}

configurations.all {
    exclude(group = "com.thingclips.smart", module = "thingsmart-modularCampAnno")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    implementation("com.alibaba:fastjson:1.1.67.android")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:3.14.9")
    implementation("com.facebook.soloader:soloader:0.10.4")
    implementation("com.facebook.infer.annotation:infer-annotation:0.18.0")
    implementation("com.facebook.fresco:fresco:3.1.3")
    implementation("com.thingclips.smart:thingsmart:5.8.1")
    implementation("com.google.zxing:core:3.3.3")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.thingclips.smart:thingsmart-ipcsdk:5.8.0")
    implementation("com.thingclips.smart:thingsmart-ipc-camera-timeline:1.1.0")
    implementation("com.thingclips.smart:thingsmart-ipc-camera-cloudtool:5.0.0")
    implementation("com.thingclips.smart:thingsmart-logsdk:5.0.1")
    implementation("com.thingclips.smart:thing-log-sdk:5.0.0")
    implementation("com.thingclips.smart:thingsmart-p2p-channel-sdk:3.4.62")
    implementation("com.thingclips.smart:thingsmart-lock-sdk:5.1.0")
    implementation("com.google.code.gson:gson:2.10.1") // Replace with the latest version
//    implementation("com.thingclips.smart:thingsmart-ipc-camera-autotest:5.0.0")
//    implementation("com.tuya.smart:tuyasmart-shortcutparser:0.0.1")
//    implementation("com.tuya.smart:tuyasmart-ipcsdk:4.0.0-4")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}