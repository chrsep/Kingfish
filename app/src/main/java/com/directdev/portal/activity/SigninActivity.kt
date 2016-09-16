package com.directdev.portal.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.*
import java.io.IOException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException
import kotlin.properties.Delegates

class SigninActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics: FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setBannerFont()
        if (DataApi.isActive) switchButtonText()
        formSignIn.onClick { signIn() }
        formPass.onKey { view, i, event ->
            if (event?.action == KeyEvent.ACTION_DOWN &&
                    event?.keyCode == KeyEvent.KEYCODE_ENTER)
                signIn()
            false
        }
    }

    private fun signIn() {
        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
        if (!connectivityManager.isNetworkAvailable()) {
            signinActivity.snack("No Network Connection")
            return
        }
        if (DataApi.isActive) return
        saveCredentials()
        switchButtonText()
        callToServer()
    }

    private fun setBannerFont() {
        val font = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        mainBanner.typeface = font
    }

    private fun saveCredentials() {
        formPass.text.toString().savePref(this, R.string.password)
        formUsername.text.toString().savePref(this, R.string.username)
    }

    private fun switchButtonText() {
        runOnUiThread {
            textSwitch.showNext()
            iconSwitch.showNext()
        }
    }

    private fun setAnalyticsUserProperties() {
        mFirebaseAnalytics.setUserProperty("degree", this.readPref(R.string.major, "") as String)
        mFirebaseAnalytics.setUserProperty("major", this.readPref(R.string.degree, "") as String)
        mFirebaseAnalytics.setUserProperty("generation", (this.readPref(R.string.nim, "") as String).substring(0, 1))
    }

    private fun callToServer() {
        DataApi.initializeApp(this).subscribe({
            true.savePref(ctx, R.string.isLoggedIn)
            DataApi.isActive = false
            setAnalyticsUserProperties()
            startActivity<MainActivity>()
        }, {
            false.savePref(ctx, R.string.isLoggedIn)
            DataApi.isActive = false
            switchButtonText()
            info { it }
            when (it) {
                is TimeoutException -> {
                    signinActivity.snack("Request Timed Out", Snackbar.LENGTH_LONG) {
                        action("retry", Color.YELLOW, { callToServer() })
                    }
                }
                is UnknownHostException -> signinActivity.snack("Failed to connect, try again later", Snackbar.LENGTH_LONG)
                is IOException -> signinActivity.snack("Wrong email or password", Snackbar.LENGTH_LONG)
                else -> {
                    signinActivity.snack("We have no idea what went wrong, but we have received the error log, please try again", Snackbar.LENGTH_INDEFINITE)
                    Crashlytics.logException(it)
                }
            }
        })
    }
}
