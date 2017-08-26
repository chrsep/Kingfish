package com.directdev.portal.di

import com.directdev.portal.features.signIn.SignInActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule{
    @ContributesAndroidInjector(modules = arrayOf(
            SigninModule::class,
            NetworkModule::class,
            FirebaseAnalyticsModule::class,
            RealmModule::class
    ))
    abstract fun bindSigninActivity(): SignInActivity
}