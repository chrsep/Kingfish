package com.directdev.portal.network

import com.directdev.portal.model.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.realm.Realm
import okhttp3.OkHttpClient
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object BinusDataService {
    private var isActive = false
    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            //TODO: Delete this if timeout takes too long
            .client(OkHttpClient()
                    .newBuilder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
                    .build())
            .baseUrl("https://newbinusmaya.binus.ac.id/services/ci/index.php/")
            .build()
            .create(BinusApi::class.java)

    fun initiateUpdate(): Observable<Boolean?> {
        isActive = true
        return Observable.zip(
                api.getFinances().subscribeOn(Schedulers.io()),
                api.getSchedules().subscribeOn(Schedulers.io()),
                api.getExam(ExamRequestBody("RS1", "1510")).subscribeOn(Schedulers.io()),
                api.getGrades("1520").subscribeOn(Schedulers.io()),
                {
                    finances: List<FinanceModel>,
                    schedules: List<ScheduleModel>,
                    exams: List<ExamModel>,
                    grade: GradeModel ->
                    isActive = false
                    grade.credit.term = grade.term
                    val dates = mutableListOf<ActivityDate>()
                    finances.forEach {
                        dates.add(parseDate(it.postedDate))
                        dates.add(parseDate(it.dueDate))
                    }
                    exams.forEach { dates.add(parseDate(it.date, "yyyy-MM-dd")) }
                    schedules.forEach { dates.add(parseDate(it.date)) }

                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        it.delete(FinanceModel::class.java)
                        it.copyToRealm(finances)

                        it.delete(ScheduleModel::class.java)
                        it.copyToRealm(schedules)

                        it.delete(ExamModel::class.java)
                        it.copyToRealm(exams)

                        it.delete(GradingModel::class.java)
                        it.copyToRealm(grade.gradings)

                        it.delete(ScoreModel::class.java)
                        it.copyToRealm(grade.scores)

                        it.copyToRealmOrUpdate(grade.credit)

                        it.delete(ActivityDate::class.java)
                        it.copyToRealmOrUpdate(dates)
                    }
                    true
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun parseDate(string: String, pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): ActivityDate {
        val item = ActivityDate()
        item.date = DateTime.parse(string, DateTimeFormat
                .forPattern(pattern))
                .toDate()
        item.id = string
        return item
    }
}
