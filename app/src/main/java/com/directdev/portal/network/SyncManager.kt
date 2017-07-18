package com.directdev.portal.network

import android.content.Context
import android.util.Log
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.model.CourseModel
import com.directdev.portal.utils.savePref
import io.realm.RealmResults
import org.joda.time.DateTime
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
        DataApi.getTokens(ctx).subscribe({
            Log.d("Token", it.toString())
            request(data, it, type)
        }, {
            Crashlytics.log("Get tokens failed: $data")
            Crashlytics.logException(it)
            onFailure.call(it)
        })
    }

    private fun request(data: SyncData, tokens: DataApi.RandomTokens, type: String = "") {
        val (ctx, onSuccess, onFailure, courses) = data
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
