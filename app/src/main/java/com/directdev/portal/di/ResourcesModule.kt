package com.directdev.portal.di

import android.content.Context
import com.directdev.portal.features.MainActivity
import com.directdev.portal.features.resources.ResourcesContract
import com.directdev.portal.features.resources.ResourcesFragment
import com.directdev.portal.features.resources.ResourcesPresenter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Named

/**
 * Created by chris on 04/10/17.
 */
@Module
class ResourcesModule {
    @Provides
    fun providePresenter(presenter: ResourcesPresenter): ResourcesContract.Presenter = presenter

    @Provides
    fun provideView(fragment: ResourcesFragment): ResourcesContract.View = fragment

    @Provides
    fun provideContext(activity: MainActivity): Context = activity

    @Provides
    fun provideActivity(fragment: ResourcesFragment): MainActivity = fragment.activity as MainActivity

    @Provides
    @Named("MainThread")
    fun provideFragmentRealm(activity: MainActivity): Realm = activity.realm
}
