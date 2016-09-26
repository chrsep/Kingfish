package com.directdev.portal.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.directdev.portal.R
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.*
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

class SigninActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics: FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val realm = Realm.getDefaultInstance()
        if (!realm.isEmpty)
            realm.executeTransaction {
                it.deleteAll()
                clearPref()
            }
        realm.close()
        setContentView(R.layout.activity_signin)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setBannerFont()
        if (DataApi.isActive) switchLoginTextView()
        formSignIn.onClick { signIn() }
        formPass.onEnter { signIn() }
        getNotif()
    }

    private fun signIn() {
        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
        if (!connectivityManager.isNetworkAvailable()) {
            signinActivity.snack("No Network Connection")
            return
        }
        if (DataApi.isActive) return
        saveCredentials()
        switchLoginTextView()
        signInCallToServer()
    }

    private fun setBannerFont() {
        val font = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        mainBanner.typeface = font
    }

    private fun saveCredentials() {
        formPass.text.toString().savePref(this, R.string.password)
        formUsername.text.toString().savePref(this, R.string.username)
    }

    private fun switchLoginTextView() {
        runOnUiThread {
            textSwitch.showNext()
            iconSwitch.showNext()
        }
    }

    private fun setAnalyticsUserProperties() {
        mFirebaseAnalytics.setUserProperty("degree", this.readPref(R.string.major, "") as String)
        mFirebaseAnalytics.setUserProperty("major", this.readPref(R.string.degree, "") as String)
        mFirebaseAnalytics.setUserProperty("generation", (this.readPref(R.string.nim, "") as String).substring(0, 3))
    }

    private fun signInCallToServer() {
        DataApi.initializeApp(this).subscribe({
            true.savePref(ctx, R.string.isLoggedIn)
            setAnalyticsUserProperties()
            Answers.getInstance().logLogin(LoginEvent()
                    .putSuccess(true)
                    .putCustomAttribute("Degree", this.readPref(R.string.major, "") as String)
                    .putCustomAttribute("Major", this.readPref(R.string.degree, "") as String)
                    .putCustomAttribute("Generation", (this.readPref(R.string.nim, "") as String).substring(0, 3)))
            startActivity<MainActivity>()
        }, {
            false.savePref(ctx, R.string.isLoggedIn)
            switchLoginTextView()
            Answers.getInstance().logLogin(LoginEvent()
                    .putSuccess(false)
                    .putCustomAttribute("Error Message", it.message)
                    .putCustomAttribute("Error Log", it.toString()))
            val snackString = DataApi.decideFailedString(it)
            if (it is SocketTimeoutException) {
                signinActivity?.snack(snackString, Snackbar.LENGTH_LONG) {
                    action("retry", Color.YELLOW, { signInCallToServer() })
                }
            } else {
                signinActivity?.snack(snackString, Snackbar.LENGTH_LONG)
            }
        })
    }

    private fun getNotif() {
        val notifyExtra = intent.getBundleExtra("Notify")
        if (notifyExtra != null && notifyExtra.getString("message") != null) {
            info { notifyExtra }
            info { notifyExtra.getString("message") }
            alert(notifyExtra.getString("message"), notifyExtra.getString("title")) {
                negativeButton("Ok, Got it")
            }.show()
        }
    }

}
