package com.directdev.portal.network

import android.content.Context
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.model.CourseModel
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.showDialog
import io.realm.RealmResults
import org.jetbrains.anko.*
import rx.Single
import rx.functions.Action1

object SyncManager {
    val INIT = "INIT"
    val COMMON = "COMMON"
    val RESOURCES = "RESOURCES"

    data class SyncData(val ctx: Context,
                        val onSuccess: Action1<Unit>,
                        val onFailure: Action1<Throwable>,
                        val courses: RealmResults<CourseModel>? = null)

    fun sync(ctx: Context,
             type: String,
             onSuccess: Action1<Unit>,
             onFailure: Action1<Throwable>,
             courses: RealmResults<CourseModel>? = null) {
        val data = SyncData(ctx, onSuccess, onFailure, courses)
        var answer: TextView? = null
        var token: String? = null
        DataApi.getToken(ctx).flatMap {
            token = it
            DataApi.fetchCaptcha(ctx.readPref(R.string.cookie))
        }.subscribe({
            ctx.showDialog("Captcha") {
                customView {
                    linearLayout {
                        imageView {
                            setImageBitmap(it)
                            lparams(width = dip(90), height = dip(32)) {
                                horizontalMargin = dip(5)
                                topMargin = dip(8)
                                leftMargin = dip(16)
                            }
                        }
                        answer = editText {
                            lparams(width = matchParent) { rightMargin = dip(16) }
                            hint = "Answer"
                        }
                    }
                    yesButton { request(data, answer?.text.toString(), token, type) }
                    noButton { request(data, answer?.text.toString(), token) }
                    onCancel {  request(data, answer?.text.toString(), token)}
                }
            }
        }, {
            Crashlytics.log("sync function fails: $type")
            Crashlytics.logException(it)
        })
    }

    private fun request(data: SyncData, captcha: String = "", token: String?, type :String = "") {
        Crashlytics.setString("captcha", captcha)
        Crashlytics.setString("token", token)
        val (ctx, onSuccess, onFailure, courses) = data
        DataApi.signIn(ctx, token ?: "", captcha).flatMap {
            when (type) {
                INIT -> DataApi.initializeApp(ctx)
                COMMON -> DataApi.fetchData(ctx)
                RESOURCES -> courses?.let { DataApi.fetchResources(ctx, it) }
                else -> Single.error(NoSuchMethodException())
            }
        }.doOnSuccess {
            Crashlytics.setString("captcha", "noValue")
            Crashlytics.setString("token", "noValue")
        }.subscribe(onSuccess, onFailure)
    }
}
