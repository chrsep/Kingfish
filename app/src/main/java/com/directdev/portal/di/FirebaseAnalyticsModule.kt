package com.directdev.portal.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/25/17.
 *------------------------------------------------------------------------------------------------*/
@Module
class FirebaseAnalyticsModule {
    @Provides
    fun providesFirebaseAnalytics(context: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
}