package com.directdev.portal.di

import com.directdev.portal.features.signin.SigninActivity
import com.directdev.portal.features.signin.SigninContract
import com.directdev.portal.features.signin.SigninPresenter
import dagger.Module
import dagger.Provides

@Module
class SigninModule {
    @Provides
    fun providePresenter(view : SigninContract.View): SigninContract.Presenter =
            SigninPresenter(view)

    @Provides
    fun provideView(activity: SigninActivity): SigninContract.View = activity
}