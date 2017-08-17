package com.directdev.portal.network

import android.content.Context
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.directdev.portal.models.CourseModel
import com.google.firebase.analytics.FirebaseAnalytics
import io.realm.RealmResults
import rx.Single
import rx.functions.Action1
import java.util.*

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
        DataApi.getTokens(ctx).subscribe({
            request(data, it, type)
        }, {
            val random_num = Random().nextInt(1000000000)
            val mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx)
            val params = Bundle()
            params.putInt("id", random_num)
            params.putString("error", it.toString())
            params.putString("message", it.message)
            mFirebaseAnalytics.logEvent("failed_login", params)

            Crashlytics.log("Get tokens failed: " + random_num)
            Crashlytics.logException(it)
            onFailure.call(it)
        })
    }

    private fun request(data: SyncData, tokens: DataApi.RandomTokens, type: String = "") {
        val (ctx, onSuccess, onFailure, courses) = data
        // Analytics
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(ctx)
        val params = Bundle()
        params.putString("type", type)
        mFirebaseAnalytics.logEvent("data_update", params)

        DataApi.signIn(ctx, tokens).flatMap {
            when (type) {
                INIT -> DataApi.initializeApp(ctx)
                COMMON -> DataApi.fetchData(ctx)
                RESOURCES -> courses?.let { DataApi.fetchResources(ctx, it) }
                else -> Single.error(NoSuchMethodException())
            }
        }.subscribe(onSuccess, onFailure)
    }
}
