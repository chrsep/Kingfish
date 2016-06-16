package com.directdev.portal

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers
import io.fabric.sdk.android.Fabric;

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers());
        Fabric.with(this, Crashlytics());

    }
}
