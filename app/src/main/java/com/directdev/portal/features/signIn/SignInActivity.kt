package com.directdev.portal.features.signIn

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.Html
import android.text.method.LinkMovementMethod
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.LoginEvent
import com.directdev.portal.BuildConfig
import com.directdev.portal.Portal
import com.directdev.portal.R
import com.directdev.portal.features.MainActivity
import com.directdev.portal.utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.*
import java.net.SocketTimeoutException
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Handles the user Signin. This class is mostly about login calls and anonymous analytics.
 *------------------------------------------------------------------------------------------------*/

class SignInActivity : Activity(), SignInContract.View, AnkoLogger {
    @Inject override lateinit var fbAnalytics: FirebaseAnalytics
    @Inject override lateinit var presenter: SignInContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        presenter.onCreate(intent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signInButton.setOnClickListener { presenter.signIn() }
        passwordField.onEnter { presenter.signIn() }

        // Create link for textview
        githubIssueLink.movementMethod = LinkMovementMethod.getInstance()
        githubIssueLink.isClickable = true
        githubIssueLink.text = if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml("Having trouble? Visit <a href='https://goo.gl/93vrOc'> Github Issue </a>", Html.FROM_HTML_MODE_LEGACY) // for 24 api and more
        } else {
            Html.fromHtml("Having trouble? Visit <a href='https://goo.gl/93vrOc'> Github Issue </a>") // or for older api
        }

        // Create link for textview
        privacypolicy.movementMethod = LinkMovementMethod.getInstance()
        privacypolicy.isClickable = true
        privacypolicy.text = if (Build.VERSION.SDK_INT >= 24) {
            Html.fromHtml("<a href='https://goo.gl/93vrOc'> Privacy Policy </a>", Html.FROM_HTML_MODE_LEGACY) // for 24 api and more
        } else {
            Html.fromHtml("Having trouble? Visit <a href='https://goo.gl/93vrOc'> Privacy Policy </a>") // or for older api
        }

        mainBanner.typeface = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
    }

    override fun navigateToMainActivity() = startActivity<MainActivity>()

    override fun checkNetwork() = connectivityManager.isNetworkAvailable()

    override fun getUsername() = usernameField.text.toString()

    override fun getPassword() = passwordField.text.toString()

    override fun showSnack(message: String) = signinActivity.snack(message)

    override fun showAlert(message: String, title: String) {
        alert(message, title) { negativeButton("Ok, Got it") {} }.show()
    }

    override fun showError(err: Throwable) {
        signinActivity.snack(err.generateMessage(), Snackbar.LENGTH_INDEFINITE) {
            when (err) {
                is SocketTimeoutException -> action("retry", Color.YELLOW, {
                    presenter.signIn()
                })
            }
        }
    }

    override fun hideKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
    }

    override fun animateSignInButton() = runOnUiThread {
        textSwitcher.showNext()
        iconSwitcher.showNext()
    }

    override fun logSuccessSignIn() {
        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())
        fbAnalytics.setUserProperty("degree", readPref(R.string.major, ""))
        fbAnalytics.setUserProperty("major", readPref(R.string.degree, ""))
        fbAnalytics.setUserProperty("generation", readPref(R.string.nim, "").substring(0, 3))
        Answers.getInstance().logLogin(LoginEvent()
                .putSuccess(true)
                .putCustomAttribute("Degree", readPref(R.string.major, ""))
                .putCustomAttribute("Major", readPref(R.string.degree, ""))
                .putCustomAttribute("Generation", readPref(R.string.nim, "").substring(0, 3)))
    }

    override fun logFailedSignIn(err: Throwable) {
        fbAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle())
        Answers.getInstance().logLogin(LoginEvent()
                .putSuccess(false)
                .putCustomAttribute("Error Message", err.message)
                .putCustomAttribute("Error Log", err.toString())
                .putCustomAttribute("Build Number", BuildConfig.VERSION_CODE))
    }

    override fun logSignOut() = fbAnalytics.logEvent("logout", Bundle())

    override fun cleanData() = (application as Portal).cleanData()
}