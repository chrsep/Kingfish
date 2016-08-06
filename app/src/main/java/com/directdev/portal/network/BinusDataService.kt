package com.directdev.portal.network

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.model.*
import com.directdev.portal.utils.NullConverterFactory
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmObject
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

object BinusDataService {
    private var isActive = false
    private val baseUrl = "https://newbinusmaya.binus.ac.id/services/ci/index.php/"
    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            //TODO: (NOTE) Delete OkHttpClient if timeout takes too long
            .client(buildOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(BinusApi::class.java)

    fun initiateUpdate(ctx: Context): Single<Boolean> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "") as String
        return login(ctx, cookie).flatMap {
            it.headers().get("Set-Cookie").savePref(ctx, R.string.cookie)
            updateAllData(cookie)
        }
    }

    //TODO: (NOTE) put cookie to each login request when ready
    private fun login(ctx: Context, cookie: String) =
            api.login(ctx.readPref(R.string.username, "") as String, ctx.readPref(R.string.password, "") as String)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun updateAllData(cookie: String) =
            Single.zip(
                    api.getFinances(cookie).subscribeOn(Schedulers.io()),
                    api.getSchedules(cookie).subscribeOn(Schedulers.io()),
                    api.getExam(ExamRequestBody("RS1", "1510"), cookie).subscribeOn(Schedulers.io()),
                    api.getGrades("1520", cookie).subscribeOn(Schedulers.io()),
                    {
                        fData: List<FinanceModel>,
                        sData: List<ScheduleModel>,
                        eData: List<ExamModel>,
                        gData: GradeModel ->
                        val dData = mutableListOf<ActivityDate>()
                        fData.forEach {
                            dData.add(parseDate(it.postedDate))
                            dData.add(parseDate(it.dueDate))
                        }
                        eData.forEach { dData.add(parseDate(it.date, "yyyy-MM-dd")) }
                        sData.forEach { dData.add(parseDate(it.date)) }

                        gData.credit.term = gData.term

                        Realm.getDefaultInstance().executeTransaction {
                            it.insertToDb(fData, FinanceModel::class)
                            it.insertToDb(sData, ScheduleModel::class)
                            it.insertToDb(eData, ExamModel::class)
                            it.insertToDb(gData.gradings, GradingModel::class)
                            it.insertToDb(gData.scores, ScoreModel::class)
                            it.copyToRealmOrUpdate(gData.credit)
                            it.insertToDb(dData, ActivityDate::class, true)
                        }
                        isActive = false
                        true
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun buildOkHttpClient() =
            OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
                    .followRedirects(false)
                    .build()

    private fun parseDate(input: String, pattern: String = "yyyy-MM-dd HH:mm:ss.SSS") =
            ActivityDate(DateTime.parse(input, DateTimeFormat.forPattern(pattern)).toDate(), input)

    private fun Realm.insertToDb(data: List<RealmObject>, type: KClass<out RealmModel>, UpdateOnly: Boolean = false) {
        this.delete(type.java)
        if (UpdateOnly)
            this.copyToRealmOrUpdate(data)
        else
            this.copyToRealm(data)
    }
}
