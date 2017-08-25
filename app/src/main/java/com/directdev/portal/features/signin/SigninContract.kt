package com.directdev.portal.features.signin

import android.content.Intent
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

interface SigninContract {
    interface View: BaseView<Presenter> {
        fun animateSigninButton()
        fun cleanDb()
        fun logSignout()
        fun showAlert(message: String, title: String)
        fun getUsername(): String
        fun getPassword(): String
        fun showError(err: Throwable)
        fun logSuccessSignin()
        fun logFailedSignin(err: Throwable)
        fun navigateToMainActivity()
        fun hideKeyboard()
        fun checkNetwork() : Boolean
        fun showSnack(message: String)
    }

    interface Presenter: BasePresenter {
        fun signin()
        fun onCreate(intent: Intent)
    }
}