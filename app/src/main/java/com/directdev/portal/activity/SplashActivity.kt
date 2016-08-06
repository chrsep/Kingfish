package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.directdev.portal.R
import com.directdev.portal.utils.readPref
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers())
        Fabric.with(this, Crashlytics())

        if (this.readPref(R.string.isLoggedIn, false) as Boolean)
            startActivity(intentFor<MainActivity>().singleTop())
        else startActivity(intentFor<LoginActivity>().singleTop())
    }
}