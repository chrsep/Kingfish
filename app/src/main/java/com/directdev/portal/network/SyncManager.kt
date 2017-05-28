package com.directdev.portal.network

import android.content.Context
import android.widget.TextView
import com.directdev.portal.R
import com.directdev.portal.model.CourseModel
import com.directdev.portal.utils.readPref
import io.realm.RealmResults
import org.jetbrains.anko.*
import rx.Single
import rx.functions.Action1

object SyncManager{
    val INIT = "INIT"
    val COMMON = "COMMON"
    val RESOURCES = "RESOURCES"

    fun sync(ctx: Context, type: String, onSuccess: Action1<Unit>, onFailure: Action1<Throwable>, courses: RealmResults<CourseModel>? = null ){
        DataApi.fetchCaptcha(ctx, ctx.readPref(R.string.cookie, "")).subscribe({
            ctx.runOnUiThread {
                var answer: TextView? = null
                alert("Captcha") {
                customView {
                    linearLayout() {
                        val captchaImage = imageView {
                            setImageBitmap(it)
                            lparams(width = dip(90), height = dip(32)) {
                                horizontalMargin = dip(5)
                                topMargin = dip(8)
                                leftMargin = dip(16)
                            }
                        }
                        answer = editText {
                            lparams(width = matchParent) {
                                rightMargin = dip(16)
                            }
                            hint = "Answer"
                        }
                    }
                    yesButton { iniiateSync(ctx, type, courses, answer?.text?.toString() ?: "", onSuccess, onFailure) }
                    noButton {  iniiateSync(ctx, "CANCELLED", courses, answer?.text?.toString() ?: "", onSuccess, onFailure) }
                }
            }.show()
            }
        })
    }

    fun iniiateSync(ctx: Context, type: String, courses: RealmResults<CourseModel>?, captcha: String, onSuccess: Action1<in Unit>, onFailure: Action1<Throwable>){
        val request =  when (type) {
            INIT -> DataApi.initializeApp(ctx, captcha)
            COMMON -> DataApi.fetchData(ctx, captcha)
            RESOURCES -> courses?.let { DataApi.fetchResources(ctx, it, captcha) }
            else -> Single.error(NoSuchMethodException())
        }
        request?.subscribe(onSuccess, onFailure)
    }
}
