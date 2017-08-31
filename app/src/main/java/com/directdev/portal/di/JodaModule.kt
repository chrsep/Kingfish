package com.directdev.portal.di

import dagger.Module
import dagger.Provides
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class JodaModule {

    @Provides
    fun provideDateTime(): DateTime = DateTime.now()

    @Provides
    fun provideDateTimeFormatter(): DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM")
}