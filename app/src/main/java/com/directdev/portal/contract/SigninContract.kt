package com.directdev.portal.contract

import com.directdev.portal.presenter.BasePresenter
import com.directdev.portal.view.BaseView

/**
 * Created by chris on 8/16/17.
 */
interface SigninContract {
    interface View: BaseView<Presenter> {
        fun animateSigninButton()
    }

    interface Presenter: BasePresenter
}