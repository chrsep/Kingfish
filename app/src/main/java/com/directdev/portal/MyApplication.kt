package com.directdev.portal

import com.facebook.stetho.Stetho
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import net.danlew.android.joda.JodaTimeAndroid

/**-------------------------------------------------------------------------------------------------
 *
 * This is called when when our app is starting, we use this to initialize our tools
 * ( Joda, Realm, Stetho )
 *
 *------------------------------------------------------------------------------------------------*/

class MyApplication : Application() {
    override fun onCreate() {
        JodaTimeAndroid.init(this)
        Realm.init(this)
        if (BuildConfig.DEBUG) Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                    .build())
        super.onCreate()
    }
}
