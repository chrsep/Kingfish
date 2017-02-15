package com.directdev.portal.activity

import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.directdev.portal.R
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.AnkoLogger

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
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers(), Crashlytics())
        if (intent.extras != null) {
            val extras = intent.extras
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>("Notify" to extras)
            else startActivity<SigninActivity>("Notify" to extras)
        } else {
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>()
            else startActivity<SigninActivity>()
        }
    }
}