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
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

object DataApi {
    var isActive = false
    private val baseUrl = "https://newbinusmaya.binus.ac.id/services/ci/index.php/"

    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            //TODO: (NOTE) Delete OkHttpClient if timeout takes too long
            .client(buildOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(DataService::class.java)


    fun fetchData(ctx: Context, firstTime: Boolean = false): Single<Boolean> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "") as String
        if (firstTime) return signIn(ctx, cookie).flatMap {
            val retrievedCookie = it.headers().get("Set-Cookie")
            retrievedCookie?.savePref(ctx, R.string.cookie)
            fetchAll(ctx, retrievedCookie ?: ctx.readPref(R.string.cookie, "") as String)
        }
        return signIn(ctx, cookie).flatMap {
            it.headers().get("Set-Cookie")?.savePref(ctx, R.string.cookie)
            fetchRecent(cookie, 1520)
        }
    }


    private fun signIn(ctx: Context, cookie: String = "") =
            api.login(
                    ctx.readPref(R.string.username, "") as String,
                    ctx.readPref(R.string.password, "") as String,
                    cookie)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun fetchAll(ctx: Context, cookie: String): Single<Boolean> {
        return api.getTerms(cookie)
                .flatMap {
                    termResponses ->
                    Single.zip(
                            callGradesForEveryTerm(cookie, termResponses),
                            {
                                gradeResponses ->
                                val realm = Realm.getDefaultInstance()
                                realm.executeTransaction { realm ->
                                    realm.copyToRealmOrUpdate(termResponses)
                                    gradeResponses.forEach { saveGradeToDb(realm, it as GradeModel) }
                                }
                                realm.close()
                            })
                            .zipWith(fetchRecent(cookie, termResponses[0].value), {
                                a, response ->
                                isActive = false
                                true
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .zipWith(api.getProfile(cookie).subscribeOn(Schedulers.io()), {
                    a, response ->
                    val profile = JSONObject(response.string())
                            .getJSONArray("Profile")
                            .getJSONObject(0)
                    profile.getString("ACAD_PROG_DESCR").savePref(ctx, R.string.major)
                    profile.getString("ACAD_CAREER_DESCR").savePref(ctx, R.string.degree)
                    profile.getString("BIRTHDATE").savePref(ctx, R.string.birthday)
                    profile.getString("NAMA").savePref(ctx, R.string.name)
                    profile.getString("NIM").savePref(ctx, R.string.nim)
                    isActive = false
                    true
                })
                .subscribeOn(Schedulers.io())
    }


    private fun fetchRecent(cookie: String, term: Int) =
            Single.zip(
                    api.getFinances(cookie).subscribeOn(Schedulers.io()),
                    api.getSchedules(cookie).subscribeOn(Schedulers.io()),
                    api.getExam(ExamRequestBody("RS1", term.toString()), cookie).subscribeOn(Schedulers.io()),
                    api.getGrades(term.toString(), cookie).subscribeOn(Schedulers.io()),
                    { fData, sData, eData, gData ->
                        val dData = getDates(eData, fData, sData)
                        val realm = Realm.getDefaultInstance()
                        realm.executeTransaction {
                            saveGradeToDb(it, gData)
                            it.insertToDb(fData, FinanceModel::class)
                            it.insertToDb(sData, ScheduleModel::class)
                            it.insertToDb(eData, ExamModel::class)
                            it.insertToDb(dData, ActivityDateModel::class, true)
                        }
                        realm.close()
                        isActive = false
                        true
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun parseDate(input: String, pattern: String = "yyyy-MM-dd HH:mm:ss.SSS") =
            ActivityDateModel(DateTime.parse(input, DateTimeFormat.forPattern(pattern)).toDate(), input)


    private fun getDates(eData: List<ExamModel>, fData: List<FinanceModel>, sData: List<ScheduleModel>): MutableList<ActivityDateModel> {
        val data = mutableListOf<ActivityDateModel>()
        fData.forEach {
            data.add(parseDate(it.postedDate))
            data.add(parseDate(it.dueDate))
        }
        eData.forEach { data.add(parseDate(it.date, "yyyy-MM-dd")) }
        sData.forEach { data.add(parseDate(it.date)) }
        return data
    }

    private fun callGradesForEveryTerm(cookie: String, terms: List<TermModel>) = terms.map {
        api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
    }

    private fun saveGradeToDb(realm: Realm, data: GradeModel) {
        data.credit.term = data.term
        realm.insertToDb(data.gradings, GradingModel::class)
        realm.insertToDb(data.scores, ScoreModel::class)
        realm.copyToRealmOrUpdate(data.credit)
    }


    private fun Realm.insertToDb(data: List<RealmObject>, type: KClass<out RealmModel>, Update: Boolean = false) {
        this.delete(type.java)
        if (Update) this.copyToRealmOrUpdate(data)
        else this.copyToRealm(data)
    }


    private fun buildOkHttpClient() =
            OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
                    .followRedirects(false)
                    .build()
}
