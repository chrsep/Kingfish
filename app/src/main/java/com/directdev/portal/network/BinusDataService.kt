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

object BinusDataService {
    private var isActive = false
    private val baseUrl = "https://newbinusmaya.binus.ac.id/services/ci/index.php/"

    /**
     * Retrofit instance that will be used for making network calls
     * **/
    private val api = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            //TODO: (NOTE) Delete OkHttpClient if timeout takes too long
            .client(buildOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(BinusApi::class.java)

    /**
     * Normal update, make a sign in call than fetch only the data required on current semester
     * **/
    fun updateData(ctx: Context): Single<Boolean> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "") as String
        return signIn(ctx, cookie).flatMap {
            it.headers().get("Set-Cookie")?.savePref(ctx, R.string.cookie)
            updateRecentData(cookie, 1520)
        }
    }

    /**
     * Called only at first signin, used to get the more permanent data such as name and major
     * because it will only be fetch once.
     * **/
    fun firstLoginSetup(ctx: Context): Single<Boolean> {
        isActive = true
        return signIn(ctx, ctx.readPref(R.string.cookie, "") as String).flatMap {
            val cookie = it.headers().get("Set-Cookie")
            cookie?.savePref(ctx, R.string.cookie)
            updateAllData(ctx, cookie ?: ctx.readPref(R.string.cookie, "") as String)
        }
    }

    /**
     * Used for signing in, send a POST call to server, receives cookies and save it
     * **/
    private fun signIn(ctx: Context, cookie: String = "") =
            api.login(
                    ctx.readPref(R.string.username, "") as String,
                    ctx.readPref(R.string.password, "") as String,
                    cookie)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    /**
     * Function for fetching complete user data(name, major, past semester grades) and save them
     * into sharedPreference and realm
     * **/
    private fun updateAllData(ctx: Context, cookie: String): Single<Boolean> {
        return api.getTerms(cookie)
                .flatMap { termResponses ->
                    Single.zip(
                            callGradesForEveryTerm(cookie, termResponses),
                            { gradeResponses ->
                                Realm.getDefaultInstance().executeTransaction { realm ->
                                    realm.copyToRealmOrUpdate(termResponses)
                                    gradeResponses.forEach { saveGradeToDb(realm, it as GradeModel) }
                                }
                            })
                            .zipWith(updateRecentData(cookie, termResponses[0].value), { a, response ->
                                isActive = false
                                true
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                }
                .zipWith(api.getProfile(cookie).subscribeOn(Schedulers.io()), { a, response ->
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

    /**
     * Calls all important data for current semester activity
     * @return ReactiveX Single<Boolean>
     * **/
    private fun updateRecentData(cookie: String, term: Int) =
            Single.zip(
                    api.getFinances(cookie).subscribeOn(Schedulers.io()),
                    api.getSchedules(cookie).subscribeOn(Schedulers.io()),
                    api.getExam(ExamRequestBody("RS1", term.toString()), cookie).subscribeOn(Schedulers.io()),
                    api.getGrades(term.toString(), cookie).subscribeOn(Schedulers.io()),
                    { fData, sData, eData, gData ->
                        val dData = getDates(eData, fData, sData)
                        Realm.getDefaultInstance().executeTransaction {
                            saveGradeToDb(it, gData)
                            it.insertToDb(fData, FinanceModel::class)
                            it.insertToDb(sData, ScheduleModel::class)
                            it.insertToDb(eData, ExamModel::class)
                            it.insertToDb(dData, ActivityDate::class, true)
                        }
                        isActive = false
                        true
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

    /**
     * Parse string that is passed on the parameter and returned it as AcrtivityDate(a RealmObject)
     * to be saved into database
     * **/
    private fun parseDate(input: String, pattern: String = "yyyy-MM-dd HH:mm:ss.SSS") =
            ActivityDate(DateTime.parse(input, DateTimeFormat.forPattern(pattern)).toDate(), input)

    /**
     * Extracts dates from every finance, schedule and exam data to be used for displaying Journal
     * **/
    private fun getDates(eData: List<ExamModel>, fData: List<FinanceModel>, sData: List<ScheduleModel>): MutableList<ActivityDate> {
        val dData = mutableListOf<ActivityDate>()
        fData.forEach {
            dData.add(parseDate(it.postedDate))
            dData.add(parseDate(it.dueDate))
        }
        eData.forEach { dData.add(parseDate(it.date, "yyyy-MM-dd")) }
        sData.forEach { dData.add(parseDate(it.date)) }
        return dData
    }

    private fun callGradesForEveryTerm(cookie: String, terms: List<TermModel>) = terms.map {
        api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
    }

    /**
     * Extract received grade data (Grading, Credit, etc) and save it to Realm
     * **/
    private fun saveGradeToDb(realm: Realm, data: GradeModel) {
        data.credit.term = data.term
        realm.insertToDb(data.gradings, GradingModel::class)
        realm.insertToDb(data.scores, ScoreModel::class)
        realm.copyToRealmOrUpdate(data.credit)
    }

    /**
     * Delete all existing data on realm and put the newly received one in
     * @param data The Object to put to realm
     * @param UpdateOnly whether to delete all data, or just update the existing database
     * @param type the type of class of the data to be deleted and inputted
     * **/
    private fun Realm.insertToDb(data: List<RealmObject>, type: KClass<out RealmModel>, UpdateOnly: Boolean = false) {
        this.delete(type.java)
        if (UpdateOnly)
            this.copyToRealmOrUpdate(data)
        else
            this.copyToRealm(data)
    }

    /**
     * OkHttpClient to set retrofit behavior such as timeout and reaction to redirect
     * **/
    private fun buildOkHttpClient() =
            OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(StethoInterceptor())
                    .followRedirects(false)
                    .build()
}
