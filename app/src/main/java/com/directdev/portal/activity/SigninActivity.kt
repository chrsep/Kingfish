package com.directdev.portal.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.directdev.portal.R
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import com.directdev.portal.utils.snack
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_signin.*
import org.jetbrains.anko.*
import kotlin.properties.Delegates

class SigninActivity : AppCompatActivity(), AnkoLogger {
    private var mFirebaseAnalytics : FirebaseAnalytics by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val font = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        setContentView(R.layout.activity_signin)
        formSignIn.onClick { signIn() }
        formPass.onKey {
            view, i, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                signIn()
            }
            false
        }
        mainBanner.typeface = font
    }

    fun signIn() {
        if (DataApi.isActive) return
        formPass.text.toString().savePref(this, R.string.password)
        formUsername.text.toString().savePref(this, R.string.username)

        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
        textSwitch.showNext()
        iconSwitch.showNext()

        DataApi.initializeApp(this).subscribe ({
            DataApi.isActive = false
            mFirebaseAnalytics.setUserProperty("degree",this.readPref(R.string.major,"") as String)
            mFirebaseAnalytics.setUserProperty("major",this.readPref(R.string.degree,"") as String)
            mFirebaseAnalytics.setUserProperty("generation", (this.readPref(R.string.nim, "") as String).substring(0, 1))
            true.savePref(this@SigninActivity, R.string.isLoggedIn)
            startActivity<MainActivity>()
        }, {
            DataApi.isActive = false
            signinActivity.snack("Wrong email or password", Snackbar.LENGTH_LONG)
            false.savePref(this@SigninActivity, R.string.isLoggedIn)
            runOnUiThread {
                textSwitch.showNext()
                iconSwitch.showNext()
            }
            throw it
        })
    }
}
