package com.directdev.portal.di

import com.directdev.portal.features.signin.SigninActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule{
    @ContributesAndroidInjector(modules = arrayOf(
            SigninModule::class,
            NetworkModule::class
    ))
    abstract fun bindSigninActivity() : SigninActivity
}