package com.directdev.portal.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.directdev.portal.network.BinusDataService
import org.jetbrains.anko.*
import retrofit2.Response
import rx.SingleSubscriber

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    fun update() {
        Log.d("Update", "Starts")
        BinusDataService.login()
                .subscribe(object : SingleSubscriber<Response<String>>() {
                    override fun onSuccess(value: Response<String>) {
                        Log.d("Update", value.headers().get("Set-Cookie"))
//                        Log.d("Update", value.headers().get("Location"))
                        Log.d("Update", value.headers().get("Content-Type"))

                    }

                    override fun onError(error: Throwable?) {
                        Log.d("Update", "asd")
                        throw error as Throwable
                    }

                })
    }
}
