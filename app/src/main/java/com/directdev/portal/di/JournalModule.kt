package com.directdev.portal.di

import android.content.Context
import com.directdev.portal.features.MainActivity
import com.directdev.portal.features.journal.JournalContract
import com.directdev.portal.features.journal.JournalFragment
import com.directdev.portal.features.journal.JournalPresenter
import com.directdev.portal.features.journal.JournalRecyclerAdapter
import dagger.Module
import dagger.Provides
import io.realm.Realm
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class JournalModule {
    @Provides
    fun providePresenter(presenter: JournalPresenter): JournalContract.Presenter = presenter

    @Provides
    fun provideView(fragment: JournalFragment): JournalContract.View = fragment

    @Provides
    fun provideActivity(fragment: JournalFragment): MainActivity = fragment.activity as MainActivity

    @Provides
    fun provideContext(activity: MainActivity): Context = activity

    @Provides
    @Named("MainThread")
    fun provideFragmentRealm(activity: MainActivity): Realm = activity.realm

    @Provides
    fun provideJournalRecyclerAdapter(ctx: Context): JournalRecyclerAdapter =
            JournalRecyclerAdapter(ctx)
}