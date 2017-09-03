package com.directdev.portal.features.signIn

import android.content.Intent
import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.ProfileInteractor
import com.directdev.portal.interactors.TermInteractor
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/16/17.
 *------------------------------------------------------------------------------------------------*/
class SignInPresenter @Inject constructor(
        private val view: SignInContract.View,
        private val authInteractor: AuthInteractor,
        private val profileInteractor: ProfileInteractor,
        private val termInteractor: TermInteractor
) : SignInContract.Presenter {
    private var subscribedToAuth = false

    override fun onCreate(intent: Intent) {
        val extra = intent.getBundleExtra("Notify")
        if (extra?.getString("message") != null)
            view.showAlert(extra.getString("message"), extra.getString("title"))
        if (intent.getStringExtra("signout") != null)
            view.logSignOut()
        view.cleanData()
    }

    override fun signIn() {
        view.hideKeyboard()
        if (!view.checkNetwork()) {
            view.showSnack("No Network Connection")
            return
        }
        if (subscribedToAuth) return
        authInteractor.execute(view.getUsername(), view.getPassword()).flatMap {
            profileInteractor.sync(it)
        }.flatMap {
            termInteractor.sync(it)
        }.doOnSubscribe {
            view.animateSignInButton()
            view.hideKeyboard()
            subscribedToAuth = true
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