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
        DataApi.getToken(ctx).map {
            request(data,it, type)
        }.subscribe()
    }

    private fun request(data: SyncData, ids: DataApi.RandomIds, type :String = "") {
        val (ctx, onSuccess, onFailure, courses) = data
        DataApi.signIn(ctx, ids).flatMap {
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
