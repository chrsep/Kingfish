package com.directdev.portal.features.signin

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

interface SigninContract {
    interface View: BaseView<Presenter> {
        fun animateSigninButton()
    }

    interface Presenter: BasePresenter
}