package com.directdev.portal.module

import com.directdev.portal.activity.SigninActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by chris on 8/17/17.
 */
@Module
abstract class ActivityModule{
    @ContributesAndroidInjector(modules = arrayOf(SigninModule::class))
    abstract fun bindSigninActivity() : SigninActivity
}