package com.directdev.portal.di

import dagger.Module
import dagger.Provides
import io.realm.Realm

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/25/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class RealmModule {
    @Provides
    fun provideRealm(): Realm = Realm.getDefaultInstance()
}