package com.directdev.portal

import android.app.Activity
import android.app.Application
import com.directdev.portal.di.DaggerApplicationComponent
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.realm.Realm
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 *
 * This is called when when our app is starting, we use this to initialize our tools
 * ( Joda, Realm, Stetho )
 *
 *------------------------------------------------------------------------------------------------*/

class Portal : Application() , HasActivityInjector{
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        JodaTimeAndroid.init(this)
        Realm.init(this)
        if (BuildConfig.DEBUG) Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build())

        super.onCreate()
    }
}
