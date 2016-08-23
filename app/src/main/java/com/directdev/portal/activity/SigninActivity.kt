package com.directdev.portal.activity

import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import com.directdev.portal.R
import com.directdev.portal.network.DataApi
import com.directdev.portal.utils.savePref
import com.directdev.portal.utils.snack
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import rx.SingleSubscriber

class SigninActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val font = Typeface.createFromAsset(assets, "fonts/SpaceMono-BoldItalic.ttf")
        setContentView(R.layout.activity_login)
        mainBanner.typeface = font
        formSignIn.onClick { signIn() }
        formPass.onKey {
            view, i, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                signIn()
            }
            false
        }
    }

    fun signIn() {
        if (DataApi.isActive) return
        inputMethodManager.hideSoftInputFromWindow(signInCard.windowToken, 0)
        textSwitch.showNext()
        iconSwitch.showNext()
        formEmail.text.toString().savePref(this, R.string.username)
        formPass.text.toString().savePref(this, R.string.password)
        DataApi.fetchData(this, true)
                .subscribe(object : SingleSubscriber<Boolean>() {
                    override fun onSuccess(value: Boolean) {
                        value.savePref(this@SigninActivity, R.string.isLoggedIn)
                        startActivity<MainActivity>()
                        textSwitch.showNext()
                        iconSwitch.showNext()
                    }

                    override fun onError(error: Throwable?) {
                        this@SigninActivity.signInCard.snack("Wrong email or password", Snackbar.LENGTH_INDEFINITE)
                        false.savePref(this@SigninActivity, R.string.isLoggedIn)
                        runOnUiThread {
                            textSwitch.showNext()
                            iconSwitch.showNext()
                        }
                        DataApi.isActive = false
                    }
                })
    }
}
