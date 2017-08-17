package com.directdev.portal.features.signin

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.directdev.portal.BuildConfig
import com.directdev.portal.R
import com.directdev.portal.features.MainActivity
import com.directdev.portal.network.DataApi
import com.directdev.portal.network.SyncManager
import com.directdev.portal.utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.*
import rx.functions.Action1
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 *
 * Handles the user Signin. This class is mostly about login calls and anonymous analytics.
 *
 *------------------------------------------------------------------------------------------------*/

class SigninActivity : AppCompatActivity(), SigninContract.View, AnkoLogger {

    private lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: SigninContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        fbAnalytics = FirebaseAnalytics.getInstance(this)
        mainBanner.typeface = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        formSignIn.onClick { signIn() }
        formPass.onEnter { signIn() }
        troubleTextView.movementMethod = LinkMovementMethod.getInstance()
        troubleTextView.isClickable = true

        troubleTextView.text = if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml("Having trouble? Visit <a href='https://goo.gl/93vrOc'> Github Issue </a>", Html.FROM_HTML_MODE_LEGACY) // for 24 api and more
        } else {
            Html.fromHtml("Having trouble? Visit <a href='https://goo.gl/93vrOc'> Github Issue </a>") // or for older api
        }

        if (DataApi.isActive) animateSigninButton()
        else deleteDbData()
        if (intent.getStringExtra("signout") != null){
            val bundle = Bundle()
            fbAnalytics.logEvent("logout", bundle)
        }
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
        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())
        SyncManager.sync(ctx, SyncManager.INIT, Action1 {
            // An anonymous function, called On login success
            //
            // Analytic data to tell us where our app is popular, Ex. of data sent
            // { undergraduate,Computer Science,18(generation) }
            //

            fbAnalytics.setUserProperty("degree", this.readPref(R.string.major, ""))
            fbAnalytics.setUserProperty("major", this.readPref(R.string.degree, ""))
            fbAnalytics.setUserProperty("generation", this.readPref(R.string.nim, "").substring(0, 3))
            Answers.getInstance().logLogin(successLoginEvent())

            savePref(true, R.string.isLoggedIn)
            startActivity<MainActivity>()
        }, Action1 {
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
            .putCustomAttribute("Build Number", BuildConfig.VERSION_CODE)

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

    override fun animateSigninButton() {
        runOnUiThread {
            textSwitch.showNext()
            iconSwitch.showNext()
        }
    }
}
