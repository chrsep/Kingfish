package com.directdev.portal.di

import com.directdev.portal.features.signin.SigninActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by chris on 8/17/17.
 */
@Module
abstract class ActivityModule{
    @ContributesAndroidInjector(modules = arrayOf(
            SigninModule::class,
            RepositoryModule::class
    ))
    abstract fun bindSigninActivity() : SigninActivity
}