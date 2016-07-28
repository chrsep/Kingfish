package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.directdev.portal.network.ProfileApi
import io.fabric.sdk.android.Fabric
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers());
        Fabric.with(this, Crashlytics());
        getData()
//        if(Prefs.read(this, R.string.username, "") == "")
//            startActivity(intentFor<LoginActivity>().singleTop())
//        else startActivity(intentFor<MainActivity>().singleTop())
    }

    private fun getData() {
        val alpha = getRequest()
        Observable.zip(
                alpha.getFinance(),
                alpha.getProfile(),
                alpha.getSchedule(),
                {
                    a: ResponseBody,
                    b: ResponseBody,
                    c: ResponseBody ->
                    val d = a.string()
                    val e = b.string()
                    val f = c.string()
                    Log.d("YOLO", d)
                    Log.d("YOLO", e)
                    Log.d("YOLO", f)
                }
        ).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        object : Observer<Int> {
                            override fun onError(e: Throwable?) {
                                System.out.print("aaa")
                            }

                            override fun onNext(t: Int?) {
                                System.out.print("bbb")
                            }

                            override fun onCompleted() {
                                System.out.print("ccc")
                            }

                        }
                )
    }

    fun getRequest() : ProfileApi {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl("https://newbinusmaya.binus.ac.id")
                .build()

        return retrofit.create(ProfileApi::class.java)
    }
}