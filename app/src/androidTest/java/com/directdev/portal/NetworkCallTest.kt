package com.directdev.portal

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.directdev.portal.activity.SplashActivity
import com.directdev.portal.network.BinusApi
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class NetworkCallTest {
    @Rule @JvmField
    var activityRule = ActivityTestRule<SplashActivity>(SplashActivity::class.java)

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            //TODO: (NOTE) Delete OkHttpClient if timeout takes too long
            .client(OkHttpClient()
                    .newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
                    .build())
            .baseUrl("https://newbinusmaya.binus.ac.id/services/ci/index.php/")
            .build()
            .create(BinusApi::class.java)

    @Test
    fun serviceTest() {

    }
}
