package com.directdev.portal.di

import com.directdev.portal.network.AuthTimeStampRepository
import com.directdev.portal.repositories.GradeTimeStampRepository
import com.directdev.portal.repositories.JournalTimeStampRepository
import com.directdev.portal.repositories.TermTimeStampRepository
import com.directdev.portal.repositories.TimeStampRepository
import dagger.Module
import dagger.Provides
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class TimeStampRepositoryModule {

    @Provides
    @Named("term")
    fun provideTermTimeStampRepository(timeStampRepo: TermTimeStampRepository): TimeStampRepository =
            timeStampRepo

    @Provides
    @Named("journal")
    fun providesJournalTimeStampRepository(timeStampRepo: JournalTimeStampRepository): TimeStampRepository =
            timeStampRepo

    @Provides
    @Named("auth")
    fun providesAuthTimeStampRepository(timeStampRepo: AuthTimeStampRepository): TimeStampRepository =
            timeStampRepo

    @Provides
    @Named("grade")
    fun provideGradeTimeStampRepository(timeStampRepo: GradeTimeStampRepository): TimeStampRepository =
            timeStampRepo

}