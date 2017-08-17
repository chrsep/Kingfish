package com.directdev.portal.features.signin

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**
 * Created by chris on 8/16/17.
 */
interface SigninContract {
    interface View: BaseView<Presenter> {
        fun animateSigninButton()
    }

    interface Presenter: BasePresenter
}