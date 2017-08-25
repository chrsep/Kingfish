package com.directdev.portal.features.signin

import android.content.Intent
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

interface SignInContract {
    interface View: BaseView<Presenter> {
        fun animateSignInButton()
        fun logSignOut()
        fun showAlert(message: String, title: String)
        fun getUsername(): String
        fun getPassword(): String
        fun showError(err: Throwable)
        fun logSuccessSignIn()
        fun logFailedSignIn(err: Throwable)
        fun navigateToMainActivity()
        fun hideKeyboard()
        fun checkNetwork() : Boolean
        fun showSnack(message: String)
    }

    interface Presenter: BasePresenter {
        fun signIn()
        fun onCreate(intent: Intent)
    }
}