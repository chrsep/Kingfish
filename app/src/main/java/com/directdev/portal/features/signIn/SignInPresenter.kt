package com.directdev.portal.features.signIn

import android.content.Intent
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.ProfileInteractor
import com.directdev.portal.repositories.MainRepository
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/16/17.
 *------------------------------------------------------------------------------------------------*/
class SignInPresenter @Inject constructor(
        private val view: SignInContract.View,
        private val authInteractor: AuthInteractor,
        private val profileInteractor: ProfileInteractor,
        private val mainRepo: MainRepository
) : SignInContract.Presenter {
    private var subscribedToAuth = false

    override fun onCreate(intent: Intent) {
        val extra = intent.getBundleExtra("Notify")
        if (extra != null) view.showAlert(extra.getString("message"), extra.getString("title"))
        if (intent.getStringExtra("signout") != null) view.logSignOut()
        mainRepo.cleanData()
    }

    override fun signIn() {
        if (!view.checkNetwork()) {
            view.showSnack("No Network Connection")
            return
        }
        view.hideKeyboard()
        if (subscribedToAuth) return
        authInteractor.execute(view.getUsername(), view.getPassword()).doOnSubscribe {
            view.animateSignInButton()
            view.hideKeyboard()
            subscribedToAuth = true
        }.flatMap {
            profileInteractor.execute()
        }.doFinally {
            subscribedToAuth = false
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