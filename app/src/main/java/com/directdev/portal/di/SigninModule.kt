package com.directdev.portal.di

import com.directdev.portal.features.signin.SigninActivity
import com.directdev.portal.features.signin.SigninContract
import com.directdev.portal.features.signin.SigninPresenter
import com.directdev.portal.repositories.RemoteRepository
import dagger.Module
import dagger.Provides

/**
 * Created by chris on 8/17/17.
 */
@Module
class SigninModule {
    @Provides
    fun providePresenter(view : SigninContract.View, remoteRepository: RemoteRepository): SigninContract.Presenter =
            SigninPresenter(view, remoteRepository)

    @Provides
    fun provideView(activity: SigninActivity): SigninContract.View = activity
}