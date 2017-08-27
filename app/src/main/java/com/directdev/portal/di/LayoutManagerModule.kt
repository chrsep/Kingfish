package com.directdev.portal.di

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import dagger.Module
import dagger.Provides

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/

@Module
class LayoutManagerModule {
    @Provides
    fun providesLinearLayoutManager(ctx: Context): LinearLayoutManager = LinearLayoutManager(ctx)
}