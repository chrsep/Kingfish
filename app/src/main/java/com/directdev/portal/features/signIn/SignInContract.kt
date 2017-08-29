package com.directdev.portal.features.signIn

import android.content.Intent
import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

interface SignInContract {
    interface View: BaseView<Presenter> {
        fun showAlert(message: String, title: String)
        fun showError(err: Throwable)
        fun showSnack(message: String)
        fun getUsername(): String
        fun getPassword(): String
        fun logSignOut()
        fun logSuccessSignIn()
        fun logFailedSignIn(err: Throwable)
        fun animateSignInButton()
        fun checkNetwork() : Boolean
        fun hideKeyboard()
        fun navigateToMainActivity()
        fun cleanData()
    }

    interface Presenter: BasePresenter {
        fun onCreate(intent: Intent)
        fun signIn()
    }
}