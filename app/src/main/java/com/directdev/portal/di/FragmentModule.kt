package com.directdev.portal.di

import com.directdev.portal.features.journal.JournalFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
@Module
abstract class FragmentModule {
    @ContributesAndroidInjector(modules = arrayOf(
            JournalModule::class,
            NetworkModule::class,
            FirebaseAnalyticsModule::class,
            RealmModule::class
    ))
    abstract fun bindJournalFragment(): JournalFragment
}