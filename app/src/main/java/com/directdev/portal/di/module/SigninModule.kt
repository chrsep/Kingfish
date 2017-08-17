package com.directdev.portal.di.module

import com.directdev.portal.activity.SigninActivity
import com.directdev.portal.contract.SigninContract
import com.directdev.portal.presenter.SigninPresenter
import dagger.Module
import dagger.Provides

/**
 * Created by chris on 8/17/17.
 */
@Module
class SigninModule {
    @Provides fun providePresenter(view : SigninContract.View): SigninContract.Presenter =
            SigninPresenter(view)

    @Provides fun provideView(activity: SigninActivity): SigninContract.View = activity
}