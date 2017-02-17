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
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.properties.Delegates

/**-------------------------------------------------------------------------------------------------
 *
 * Handles the user Signin. This class is mostly about handling login calls and gathering anonymous
 * analytics.
 *
 *------------------------------------------------------------------------------------------------*/

class SigninActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics: FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mainBanner.typeface = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        formSignIn.onClick { signIn() }
        formPass.onEnter { signIn() }
        if (DataApi.isActive) animateSigninButton()
        else deleteDbData()
        getNotif()
    }

    private fun signIn() {
        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
        if (!connectivityManager.isNetworkAvailable()) {
            signinActivity.snack("No Network Connection")
            return
        }
        if (DataApi.isActive) return
        saveUsernameAndPassword()
        animateSigninButton()
        signInCallToServer()
    }

    private fun signInCallToServer() {
        DataApi.initializeApp(this).subscribe({
            // An anonymous function, called On login success
            //
            // Analytic data to tell us where our app is popular, Ex. of data sent
            // { undergraduate,Computer Science,18 }
            //

            mFirebaseAnalytics.setUserProperty("degree", this.readPref(R.string.major, ""))
            mFirebaseAnalytics.setUserProperty("major", this.readPref(R.string.degree, ""))
            mFirebaseAnalytics.setUserProperty("generation", this.readPref(R.string.nim, "").substring(0, 3))
            Answers.getInstance().logLogin(successLoginEvent())

            savePref(true, R.string.isLoggedIn)
            startActivity<MainActivity>()
        }, {
            // Another anonymous function, called On login failure
            Answers.getInstance().logLogin(failedLoginEvent(it))

            animateSigninButton()
            savePref(false, R.string.isLoggedIn)
            savePref(false, R.string.isStaff)

            //
            // Shows a SnackBar telling user what went wrong with their login attempt
            //

            signinActivity.snack(DataApi.decideCauseOfFailure(it), Snackbar.LENGTH_INDEFINITE) {
                when (it) {
                    is SocketTimeoutException -> action("retry", Color.YELLOW, { signIn() })
                    is IOException -> action("retry as staff", Color.YELLOW, {
                        savePref(true, R.string.isStaff)
                        signIn()
                    })
                }
            }
        })
    }

    private fun successLoginEvent() = LoginEvent()
            .putSuccess(true)
            .putCustomAttribute("Degree", this.readPref(R.string.major, ""))
            .putCustomAttribute("Major", this.readPref(R.string.degree, ""))
            .putCustomAttribute("Generation", this.readPref(R.string.nim, "").substring(0, 3))

    private fun failedLoginEvent(it: Throwable) = LoginEvent()
            .putSuccess(false)
            .putCustomAttribute("Error Message", it.message)
            .putCustomAttribute("Error Log", it.toString())

    private fun getNotif() {
        val notifyExtra = intent.getBundleExtra("Notify")
        if (notifyExtra != null && notifyExtra.getString("message") != null) {
            alert(notifyExtra.getString("message"), notifyExtra.getString("title")) {
                negativeButton("Ok, Got it")
            }.show()
        }
    }

    /*----------------------------------------------------------------------------------------------
     *
     * Other helper functions, just to abstract away unimportant stuff
     *
     *--------------------------------------------------------------------------------------------*/

    private fun deleteDbData() {
        val realm = Realm.getDefaultInstance()
        savePref(false, R.string.isStaff)
        if (!realm.isEmpty)
            realm.executeTransaction {
                it.deleteAll()
                clearPref()
            }
        realm.close()
    }

    private fun saveUsernameAndPassword() {
        savePref(formPass.text.toString(), R.string.password)
        savePref(formUsername.text.toString(), R.string.username)
    }

    /**---------------------------------------------------------------------------------------------
     *
     * Do you notice the little animation on the login button when clicked or when loading finished?
     * This function initiate that animation (the text and icon switching animation on signin button
     * to be exact).
     *
     *--------------------------------------------------------------------------------------------*/

    private fun animateSigninButton() {
        runOnUiThread {
            textSwitch.showNext()
            iconSwitch.showNext()
        }
    }
}
