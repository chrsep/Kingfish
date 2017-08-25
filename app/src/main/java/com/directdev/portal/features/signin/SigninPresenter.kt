package com.directdev.portal.features.signin

import android.content.Intent
import com.directdev.portal.interactors.AuthInteractor
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/16/17.
 *------------------------------------------------------------------------------------------------*/
class SigninPresenter @Inject constructor(
        val view: SigninContract.View,
        val authInteractor: AuthInteractor
) : SigninContract.Presenter {
    override fun signin() {
        authInteractor.execute(view.getUsername(), view.getPassword()).doOnSubscribe {
            view.animateSigninButton()
        }.subscribe({
            view.animateSigninButton()
        }, {
            view.animateSigninButton()
            view.showError(it)
        })
    }

    override fun onCreate(intent: Intent) {
        val extra = intent.getBundleExtra("Notify")
        if (extra != null) view.showAlert(extra.getString("message"), extra.getString("title"))
        if (intent.getStringExtra("signout") != null) view.logSignout()
        // TODO: Clean DB should be done by an interactor instead of by a view
        view.cleanDb()
    }

    override fun start() {
    }
}