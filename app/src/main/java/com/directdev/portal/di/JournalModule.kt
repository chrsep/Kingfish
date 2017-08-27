package com.directdev.portal.di

import android.content.Context
import com.directdev.portal.features.journal.JournalContract
import com.directdev.portal.features.journal.JournalFragment
import com.directdev.portal.features.journal.JournalPresenter
import dagger.Module
import dagger.Provides

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
    fun provideContext(fragment: JournalFragment): Context = fragment.activity
}