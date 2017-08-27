package com.directdev.portal

import android.app.Activity
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.directdev.portal.di.DaggerApplicationComponent
import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import net.danlew.android.joda.JodaTimeAndroid
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 *
 * This is called when when our app is starting, we use this to initialize our tools
 * ( Joda, Realm, Stetho )
 *
 *------------------------------------------------------------------------------------------------*/

class Portal : MultiDexApplication(), HasActivityInjector {
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector() = dispatchingAndroidInjector

    override fun onCreate() {
        // Initialize Dagger 2 for dependency injection
        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

        // Initialize JodaTime, used for handling date and time
        JodaTimeAndroid.init(this)

        // Initialize Realm Database
        Realm.init(this)

        // Initialize Stetho on Debug Build for enabling debugging using Chrome DevTools
        if (BuildConfig.DEBUG) Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build())

        // Initializes Fabric only for non-Debug build.
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, Answers(), crashlyticsKit)
        super.onCreate()
    }
}
