package com.directdev.portal.features

import android.app.Activity
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.features.signIn.SignInActivity
import com.directdev.portal.utils.readPref
import io.realm.Realm
import io.realm.exceptions.RealmMigrationNeededException
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.startActivity

/**-------------------------------------------------------------------------------------------------
 * This is the activity that is always loaded first when Portal starts. This class provides the loading
 * screen that you see when  opening Portal (the dark screen with Portal logo on the middle), this is
 * done using a theme (check AndroidManifest). After loading is finished, this activity will decide
 * which activity will be opened based on the user login status.
 *------------------------------------------------------------------------------------------------*/

class SplashActivity : Activity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.extras != null) {
            val extras = intent.extras
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>("Notify" to extras)
            else startActivity<SignInActivity>("Notify" to extras)
        } else {
            if (readPref(R.string.isLoggedIn, false)) startActivity<MainActivity>()
            else startActivity<SignInActivity>()
        }

        // This is for making sure that Realm.init() always get called when Portal starts
        // a bug on Android 6 makes the Application's onCreate might not be called before
        // everything else.
        try {
            Realm.getDefaultInstance().close()
        } catch (err: IllegalStateException) {
            Realm.init(applicationContext)
        } catch (err: RealmMigrationNeededException){
            Crashlytics.setBool("isLoggedIn", readPref(R.string.isLoggedIn, false))
            Crashlytics.logException(err)
            // Clean up all data when realmMigration is requested
            Realm.deleteRealm(Realm.getDefaultConfiguration())
            startActivity<SignInActivity>(params = *arrayOf(Pair("signout","signout")))
        }
        super.onCreate(savedInstanceState)
    }
}