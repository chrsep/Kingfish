package com.directdev.portal.di

import android.content.Context
import com.directdev.portal.features.signIn.SignInActivity
import com.directdev.portal.features.signIn.SignInContract
import com.directdev.portal.features.signIn.SignInPresenter
import dagger.Module
import dagger.Provides

@Module
class SigninModule {
    @Provides
    fun providePresenter(signInPresenter: SignInPresenter): SignInContract.Presenter = signInPresenter

    @Provides
    fun provideView(activity: SignInActivity): SignInContract.View = activity

    @Provides
    fun provideContext(activity: SignInActivity): Context = activity
}