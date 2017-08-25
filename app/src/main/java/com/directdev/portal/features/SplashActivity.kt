package com.directdev.portal.features

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.directdev.portal.BuildConfig
import com.directdev.portal.R
import com.directdev.portal.features.signin.SignInActivity
import com.directdev.portal.utils.readPref
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

/**-------------------------------------------------------------------------------------------------
 *
 * This is the activity that is always loaded first when Portal starts. This class provides the loading
 * screen that you see when  opening Portal (the dark screen with Portal logo on the middle), this is
 * done using a theme (check AndroidManifest). After loading is finished, this activity will decide
 * which activity will be opened based on the user login status.
 *
 *------------------------------------------------------------------------------------------------*/

class SplashActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initializes Fabric for builds that don't use the debug build type.
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, Answers(), crashlyticsKit)
        if (intent.extras != null) {
            val extras = intent.extras
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>("Notify" to extras)
            else startActivity<SignInActivity>("Notify" to extras)
        } else {
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>()
            else startActivity<SignInActivity>()
        }
        super.onCreate(savedInstanceState)
    }
}