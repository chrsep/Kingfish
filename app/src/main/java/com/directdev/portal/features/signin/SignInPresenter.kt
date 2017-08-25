package com.directdev.portal.features.signin

import android.content.Intent
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.repositories.MainRepository
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/16/17.
 *------------------------------------------------------------------------------------------------*/
class SignInPresenter @Inject constructor(
        private val view: SignInContract.View,
        private val authInteractor: AuthInteractor,
        private val mainRepo: MainRepository
) : SignInContract.Presenter {

    override fun onCreate(intent: Intent) {
        val extra = intent.getBundleExtra("Notify")
        if (extra != null) view.showAlert(extra.getString("message"), extra.getString("title"))
        if (intent.getStringExtra("signout") != null) view.logSignOut()
        // TODO: Clean DB should be done by an interactor instead of by a view
        mainRepo.cleanData()
    }

    override fun signIn() {
        if (!view.checkNetwork()) {
            view.showSnack("No Network Connection")
            return
        }
        view.hideKeyboard()
        authInteractor.execute(view.getUsername(), view.getPassword()).doOnSubscribe {
            view.animateSignInButton()
            view.hideKeyboard()
        }.subscribe({
            view.logSuccessSignIn()
            view.navigateToMainActivity()
        }, {
            view.logFailedSignIn(it)
            view.showError(it)
            view.animateSignInButton()
        })
    }


}