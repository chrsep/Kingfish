package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.directdev.portal.R
import com.directdev.portal.utils.readPref
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers(), Crashlytics())
        if (intent.extras != null) {
            val intentExtras = intent.extras
            if (readPref(R.string.isLoggedIn, false) as Boolean) startActivity<MainActivity>("Notify" to intentExtras)
            else startActivity<SigninActivity>("Notify" to intentExtras)
        } else {
            if (readPref(R.string.isLoggedIn, false) as Boolean) startActivity<MainActivity>()
            else startActivity<SigninActivity>()
        }
    }
}