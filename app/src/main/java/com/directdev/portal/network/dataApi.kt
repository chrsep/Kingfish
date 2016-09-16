package com.directdev.portal.network

import android.content.Context
import com.directdev.portal.BuildConfig
import com.directdev.portal.R
import com.directdev.portal.model.*
import com.directdev.portal.utils.NullConverterFactory
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
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
    private val baseUrl = "https://binusmaya.binus.ac.id/services/ci/index.php/"
    private val api = buildRetrofit()

    fun initializeApp(ctx: Context): Single<Unit> {
        isActive = true
        var cookie = ctx.readPref(R.string.cookie, "") as String
        return signIn(ctx, cookie).flatMap {
            val headerCookie = it.headers().get("Set-Cookie")
            if (headerCookie == null) {
                api.getTerms(cookie).subscribeOn(Schedulers.io())
            } else {
                cookie = headerCookie
                cookie.savePref(ctx, R.string.cookie)
                api.getTerms(cookie).subscribeOn(Schedulers.io())
            }
        }.flatMap {
            terms ->
            Single.zip(fetchGrades(terms, cookie), {
                grades ->
                grades
            }).zipWith(api.getProfile(cookie).subscribeOn(Schedulers.io()), {
                grades, profile ->
                saveProfile(ctx, profile)
                profile.close()
                grades
            }).zipWith(fetchCourses(terms, cookie), {
                grades, courses ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    realm ->
                    realm.insertOrUpdate(terms)
                    realm.insertOrUpdate(courses)
                    realm.delete(ScoreModel::class.java)
                    grades.forEach { realm.insertGrade(it as GradeModel) }
                }
                realm.close()
            }).zipWith(fetchRecent(ctx, cookie, terms[0].value.toString()), {
                a, b ->
            })
        }
    }

    fun fetchData(ctx: Context): Single<Unit> {
        isActive = true
        var cookie = ctx.readPref(R.string.cookie, "") as String
        val realm = Realm.getDefaultInstance()
        return signIn(ctx, cookie).flatMap {
            val headerCookie = it.headers().get("Set-Cookie")
            val term = realm.where(TermModel::class.java).max("value")
            if (headerCookie == null) {
                fetchRecent(ctx, cookie, term.toString())
            } else {
                cookie = headerCookie
                cookie.savePref(ctx, R.string.cookie)
                fetchRecent(ctx, cookie, term.toString())
            }
        }.map { terms ->
            realm.close()
        }
    }

    fun fetchResources(ctx: Context, data: RealmResults<CourseModel>): Single<Unit> {
        isActive = true
        var cookie = ctx.readPref(R.string.cookie, "") as String
        return signIn(ctx, cookie).flatMap {
            Single.zip(data.map {
                val classNumber = it.classNumber
                api.getResources(
                        it.courseId,
                        it.crseId,
                        it.term.toString(),
                        it.ssrComponent,
                        it.classNumber.toString(),
                        cookie
                ).map { data ->
                    data.classNumber = classNumber
                    data
                }.subscribeOn(Schedulers.io())
            }, {
                resources ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    resources.forEach {
                        val resModel = ResModel()
                        resModel.book.addAll((it as ResModelIntermidiary).book)
                        resModel.path.addAll(it.path)
                        resModel.resources.addAll(it.resources)
                        resModel.url.addAll(it.url)
                        resModel.webContent = it.webContent
                        resModel.classNumber = it.classNumber
                        realm.insertOrUpdate(resModel)
                    }
                }
            })
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchAssignment(ctx: Context, data: RealmResults<CourseModel>): Single<Unit> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "") as String
        return signIn(ctx, cookie).flatMap {
            Single.zip(data.map {
                val classNumber = it.classNumber
                api.getAssignment(
                        it.courseId,
                        it.crseId,
                        it.term.toString(),
                        it.ssrComponent,
                        it.classNumber.toString(),
                        cookie
                ).map { data ->
                    data.forEach { it.classNumber = classNumber }
                    data
                }.subscribeOn(Schedulers.io())
            }, {
                assignment ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction { realm ->
                    val list = mutableListOf<AssignmentIndividualModel>()
                    (assignment.filterIsInstance<List<AssignmentIndividualModel>>()).forEach {
                        list.addAll(it)
                    }
                    realm.cleanInsert(list)
                }
            })
        }
    }

    private fun fetchGrades(terms: List<TermModel>, cookie: String): List<Single<GradeModel>> =
            terms.drop(1).map {
                api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
            }

    private fun fetchCourses(terms: List<TermModel>, cookie: String) =
            Single.zip(
                    terms.drop(1).map({
                        term ->
                        api.getCourse(term.value.toString(), cookie)
                                .subscribeOn(Schedulers.io())
                                .map {
                                    it.courses.forEach { it.term = term.value }
                                    it.courses
                                }
                    }),
                    {
                        val listOfCourses = mutableListOf<CourseModel>()
                        val itList = it.filterIsInstance<List<CourseModel>>()
                        itList.forEach { listOfCourses.addAll(it) }
                        listOfCourses
                    }
            )

    private fun signIn(ctx: Context, cookie: String = "") = api.signIn(
            ctx.readPref(R.string.username, "") as String,
            ctx.readPref(R.string.password, "") as String,
            cookie)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    private fun fetchRecent(ctx: Context, cookie: String, term: String) = Single.zip(
            api.getFinances(cookie).subscribeOn(Schedulers.io()),
            api.getSessions(cookie).subscribeOn(Schedulers.io()),
            api.getExams(ExamRequestBody(term), cookie).subscribeOn(Schedulers.io()),
            api.getGrades(term.toString(), cookie).subscribeOn(Schedulers.io()),
            api.getFinanceSummary(cookie).subscribeOn(Schedulers.io()),
            api.getCourse(term, cookie).subscribeOn(Schedulers.io()),
            { finance, session, exam, grade, financeSummary, course ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.delete(JournalModel::class.java)
                    it.delete(ExamModel::class.java)
                    it.delete(FinanceModel::class.java)
                    it.delete(SessionModel::class.java)
                    it.insertOrUpdate(mapToJournal(exam, finance, session))
                    it.insertGrade(grade)
                    saveFinanceSummary(ctx, financeSummary)
                    saveCourse(course, term, it)
                }
                realm.close()
                isActive = false
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private fun saveCourse(course: CourseWrapperModel, term: String, realm: Realm) {
        course.courses.forEach {
            it.term = term.toInt()
        }
        realm.insertOrUpdate(course.courses)
    }

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

    private fun saveProfile(ctx: Context, response: ResponseBody) {
        val profile = JSONObject(response.string()).getJSONArray("Profile").getJSONObject(0)
        profile.getString("ACAD_PROG_DESCR").savePref(ctx, R.string.major)
        profile.getString("ACAD_CAREER_DESCR").savePref(ctx, R.string.degree)
        profile.getString("BIRTHDATE").savePref(ctx, R.string.birthday)
        profile.getString("NAMA").savePref(ctx, R.string.name)
        profile.getString("NIM").savePref(ctx, R.string.nim)
    }

    private fun saveFinanceSummary(ctx: Context, response: ResponseBody) {
        val summary = JSONArray(response.string()).getJSONObject(0)
        summary.getInt("charge").savePref(ctx, R.string.finance_charge)
        summary.getInt("payment").savePref(ctx, R.string.finance_payment)
    }

    private fun Realm.insertGrade(data: GradeModel) {
        data.credit.term = data.term.toInt()
        cleanInsert(data.gradings)
        insert(data.scores)
        insertOrUpdate(data.credit)
    }

    private fun Realm.cleanInsert(data: List<RealmObject>) {
        if (data.size == 0) return
        delete(data[0].javaClass)
        insert(data)
    }

    private fun buildRetrofit(): DataService {
        val client: OkHttpClient
        if (BuildConfig.DEBUG) client = buildDebugClient()
        else client = buildClient()

        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(NullConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client)
                .baseUrl(baseUrl)
                .build().create(DataService::class.java)
    }

    private fun buildDebugClient() = OkHttpClient().newBuilder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .followRedirects(false)
            .build()

    private fun buildClient() = OkHttpClient().newBuilder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .followRedirects(false)
            .build()
}
