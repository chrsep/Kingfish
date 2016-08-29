package com.directdev.portal.network

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.model.*
import com.directdev.portal.utils.NullConverterFactory
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.realm.Realm
import io.realm.RealmObject
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object DataApi {
    var isActive = false
    private val baseUrl = "https://newbinusmaya.binus.ac.id/services/ci/index.php/"
    //TODO: (NOTE) Delete OkHttpClient if timeout takes too long
    private val client = OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .followRedirects(false)
            .build()
    private val api = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(DataService::class.java)


    fun fetchData(ctx: Context, firstTime: Boolean = false): Single<Unit> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "") as String
        if (firstTime) return signIn(ctx, cookie).flatMap {
            val retrievedCookie = it.headers().get("Set-Cookie")
            retrievedCookie?.savePref(ctx, R.string.cookie)
            fetchAll(ctx, retrievedCookie ?: ctx.readPref(R.string.cookie, "") as String)
        }
        return signIn(ctx, cookie).flatMap {
            it.headers().get("Set-Cookie")?.savePref(ctx, R.string.cookie)
            fetchRecent(cookie, "1520")
        }
    }


    private fun signIn(ctx: Context, cookie: String = "") =
            api.signIn(
                    ctx.readPref(R.string.username, "") as String,
                    ctx.readPref(R.string.password, "") as String,
                    cookie)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun fetchAll(ctx: Context, cookie: String) = api.getTerms(cookie).flatMap {
        termResponses ->
        Single.zip(
                fetchEveryGradeInTerm(cookie, termResponses),
                {
                    gradeResponses ->
                    val realm = Realm.getDefaultInstance()
                    realm.executeTransaction {
                        it.copyToRealmOrUpdate(termResponses)
                        gradeResponses.forEach { realm.insertGrade(it as GradeModel) }
                    }
                    realm.close()
                })
                .zipWith(fetchRecent(cookie, termResponses[0].value.toString()), {
                    a, response ->
                    isActive = false
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }.zipWith(api.getProfile(cookie).subscribeOn(Schedulers.io()), {
        a, response ->
        val profile = JSONObject(response.string())
                .getJSONArray("Profile")
                .getJSONObject(0)
        saveProfile(ctx, profile)
        isActive = false
    }).subscribeOn(Schedulers.io())


    private fun fetchRecent(cookie: String, term: String) = Single.zip(
            api.getFinances(cookie).subscribeOn(Schedulers.io()),
            api.getSessions(cookie).subscribeOn(Schedulers.io()),
            api.getExams(ExamRequestBody(term), cookie).subscribeOn(Schedulers.io()),
            api.getGrades(term.toString(), cookie).subscribeOn(Schedulers.io()),
            { finance, session, exam, grade ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.delete(JournalModel::class.java)
                    it.delete(ExamModel::class.java)
                    it.delete(FinanceModel::class.java)
                    it.delete(SessionModel::class.java)
                    it.copyToRealmOrUpdate(mapToJournal(exam, finance, session))
                    it.insertGrade(grade)
                }
                realm.close()
                isActive = false
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private fun mapToJournal(exam: List<ExamModel>, finance: List<FinanceModel>, session: List<SessionModel>): MutableList<JournalModel> {
        val items = mutableListOf<JournalModel>()
        finance.forEach { items.add(JournalModel(it.dueDate).setDate()) }
        exam.forEach { items.add(JournalModel(it.date).setDate("yyyy-MM-dd")) }
        session.forEach { items.add(JournalModel(it.date).setDate()) }
        items.forEach { item ->
            session.forEach { if (item.id == it.date) item.session.add(it) }
            finance.forEach { if (item.id == it.dueDate) item.finance.add(it) }
            exam.forEach { if (item.id == it.date) item.exam.add(it) }
        }
        return items
    }

    private fun saveProfile(ctx: Context, profile: JSONObject) {
        profile.getString("ACAD_PROG_DESCR").savePref(ctx, R.string.major)
        profile.getString("ACAD_CAREER_DESCR").savePref(ctx, R.string.degree)
        profile.getString("BIRTHDATE").savePref(ctx, R.string.birthday)
        profile.getString("NAMA").savePref(ctx, R.string.name)
        profile.getString("NIM").savePref(ctx, R.string.nim)
    }

    private fun fetchEveryGradeInTerm(cookie: String, terms: List<TermModel>) = terms.map {
        api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
    }

    private fun Realm.insertGrade(data: GradeModel) {
        data.credit.term = data.term
        cleanInsert(data.gradings)
        cleanInsert(data.scores)
        copyToRealmOrUpdate(data.credit)
    }

    private fun Realm.cleanInsert(data: List<RealmObject>) {
        if (data.size == 0) return
        delete(data[0].javaClass)
        copyToRealm(data)
    }
}
