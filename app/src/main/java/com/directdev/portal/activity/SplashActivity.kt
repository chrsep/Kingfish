package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.directdev.portal.network.BinusDataService
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.*
import rx.Observer

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Answers())
        Fabric.with(this, Crashlytics())
        //For testing Purposes
        verticalLayout {
            padding = dip(30)
            editText {
                hint = "Name"
                textSize = 24f
            }
            editText {
                hint = "Password"
                textSize = 24f
            }
            button("Login") {
                onClick { update() }
                textSize = 26f
            }
        }
//        if(Prefs.read(this, R.string.username, "") == "")
//            startActivity(intentFor<LoginActivity>().singleTop())
//        else startActivity(intentFor<MainActivity>().singleTop())
    }

    fun update(){
        Log.d("Update", "Starts")
        BinusDataService.initiateUpdate()
                .subscribe(
                        object : Observer<Boolean?> {
                            override fun onNext(t: Boolean?) {
                                Log.d("Update", t.toString())
                            }

                            override fun onError(e: Throwable?) {
                                Log.d("Update", e.toString())
                            }

                            override fun onCompleted() {
                                Log.d("Update", "EndComplete")
                            }

                        }
                )
    }
}