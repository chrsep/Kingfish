package com.directdev.portal.di

import android.content.Context
import com.directdev.portal.features.MainActivity
import com.directdev.portal.features.grades.GradesContract
import com.directdev.portal.features.grades.GradesFragment
import com.directdev.portal.features.grades.GradesPresenter
import com.directdev.portal.features.grades.GradesRecyclerAdapter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 9/2/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class GradesModule {
    @Provides
    fun provideGradesRecyclerAdapter() = GradesRecyclerAdapter()

    @Provides
    fun providePresenter(presenter: GradesPresenter): GradesContract.Presenter = presenter

    @Provides
    fun provideView(view: GradesFragment): GradesContract.View = view

    @Provides
    fun provideContext(view: GradesFragment): Context = view.activity

    @Provides
    fun provideMainActivity(view: GradesFragment): MainActivity = view.activity as MainActivity

    @Provides
    @Named("MainThread")
    fun provideFragmentRealm(activity: MainActivity): Realm = activity.realm

}