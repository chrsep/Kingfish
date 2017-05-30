package com.directdev.portal.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.crashlytics.android.Crashlytics
import com.directdev.portal.BuildConfig
import com.directdev.portal.R
import com.directdev.portal.model.*
import com.directdev.portal.utils.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.HttpException
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

/*-------------------------------------------------------------------------------------------------
 *
 * This is by far the most nightmarish part of this codebase. Due to the complex sequence of calls
 * and the concurrency involved, we are heavily reliant on ReactiveX, and since this is the first
 * time we use ReactiveX, this code has become quite a mess (at least that's how we feel). Further
 * refinement of this object will be required, any help will be appreciated :)
 *
 * TODO: REFACTOR | Reorganizes DataApi to create more readable code
 *
 *------------------------------------------------------------------------------------------------*/
/**-------------------------------------------------------------------------------------------------
 * A singleton that handles all of Portal API calls, Using ReactiveX and Retrofit.
 *------------------------------------------------------------------------------------------------*/

object DataApi {
    var isActive = false
    private val baseUrl = "https://binusmaya.binus.ac.id/services/ci/index.php/"
    private val api = buildRetrofit()
    private fun isStaff(ctx: Context) = ctx.readPref(R.string.isStaff, false)

    fun initializeApp(ctx: Context): Single<Unit> {
        val cookie = ctx.readPref(R.string.cookie, "")
        return api.getTerms(cookie).subscribeOnIo().flatMap { terms ->
            Crashlytics.log("initializeApp Term Data " + terms.toString())
            val gradeObservable = when (terms.size) {
                1 -> fetchGrades(terms, cookie)[0].map { arrayOf<Any>(it) }
                else -> Single.zip(fetchGrades(terms, cookie)) { grades -> grades }
            }
            Single.zip(gradeObservable,
                    api.getProfile(cookie).subscribeOnIo(),
                    fetchCourses(terms, cookie),
                    fetchRecent(ctx, cookie, terms[0].value.toString())) {
                grades, profile, courses, _ ->
                saveProfile(ctx, profile)
                profile.close()
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.insertOrUpdate(terms)
                    it.insertOrUpdate(courses)
                    it.delete(ScoreModel::class.java)
                    grades.forEach { grade -> it.insertGrade(grade as GradeModel) }
                }
                realm.close()

            }
        }.doOnSubscribe {
            isActive = true
        }.doAfterTerminate {
            isActive = false
        }.doOnSuccess {
            setLastUpdate(ctx)
        }
    }


    fun fetchData(ctx: Context): Single<Unit> {
        val cookie = ctx.readPref(R.string.cookie, "")
        val realm = Realm.getDefaultInstance()
        val term = realm.where(TermModel::class.java).max("value")
        return fetchRecent(ctx, cookie, term.toString()).doOnSuccess { _ ->
            setLastUpdate(ctx)
        }.doOnSubscribe {
            isActive = true
        }.doAfterTerminate {
            realm.close()
            isActive = false
        }
    }

    fun fetchResources(ctx: Context, data: RealmResults<CourseModel>): Single<Unit> {
        isActive = true
        val cookie = ctx.readPref(R.string.cookie, "")
        return Single.zip(data.map {
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
            }.subscribeOnIo()
        }) { resources ->
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
            realm.close()
        }.defaultThreads().doOnSubscribe {
            isActive = true
        }.doAfterTerminate {
            isActive = false
        }
    }

    fun getToken(ctx: Context): Single<String> {
        val cookie = ctx.readPref(R.string.cookie, "")
        val pattern = "<input type=\"hidden\" name=\"token\" value=\".*\""
        return api.getToken(cookie).map {
            val input = Regex(pattern).find(it.body().string())
            ctx.savePref(it.headers().get("Set-Cookie") ?: cookie, R.string.cookie)
            input?.value?.substring(41, input.value.length - 1) ?: ""
        }.defaultThreads()
    }

    fun signIn(ctx: Context, token: String, captcha: String): Single<Response<out Any>> = api.signIn(
            ctx.readPref(R.string.username, ""),
            ctx.readPref(R.string.password, ""),
            ctx.readPref(R.string.cookie),
            captcha, token).flatMap {
        if (isStaff(ctx))
            api.switchRole(ctx.readPref(R.string.cookie))
        else
            Single.just(it)
    }.defaultThreads()

    fun fetchCaptcha(cookie: String): Single<Bitmap> = api.getCaptchaImage(cookie).map {
        val inputStream = it.body().byteStream()
        BitmapFactory.decodeStream(inputStream)
    }.defaultThreads()


    private fun fetchGrades(terms: List<TermModel>, cookie: String) = terms.map {
        api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
    }

    private fun fetchCourses(terms: List<TermModel>, cookie: String) = when (terms.size) {
        1 -> api.getCourse(terms[0].value.toString(), cookie).subscribeOnIo()
                .map {
                    it.courses.forEach { it.term = terms[0].value }
                    it.courses
                }

        else -> Single.zip(terms.drop(1).map { term ->
            api.getCourse(term.value.toString(), cookie).subscribeOnIo()
                    .map {
                        it.courses.forEach { it.term = term.value }
                        it.courses
                    }
        }) { it.filterIsInstance<List<CourseModel>>().flatten() }
    }


    private fun fetchRecent(ctx: Context, cookie: String, term: String) = Single.zip(
            api.getFinances(cookie).subscribeOnIo(),
            api.getSessions(cookie).subscribeOnIo(),
            api.getExams(ExamRequestBody(term), cookie).subscribeOnIo(),
            api.getGrades(term, cookie).subscribeOnIo(),
            api.getFinanceSummary(cookie).subscribeOnIo(),
            api.getCourse(term, cookie).subscribeOnIo(),
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
            }).defaultThreads()

    /*----------------------------------------------------------------------------------------------
     * Helper function for saving data to Realm
     *--------------------------------------------------------------------------------------------*/

    private fun saveCourse(course: CourseWrapperModel, term: String, realm: Realm) {
        course.courses.forEach { it.term = term.toInt() }
        realm.insertOrUpdate(course.courses)
    }

    /**---------------------------------------------------------------------------------------------
     * Combine exam, finance, and session object into one journal object for saving to realm
     *--------------------------------------------------------------------------------------------*/

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

    /**---------------------------------------------------------------------------------------------
     * Save user personal data to preferences
     *--------------------------------------------------------------------------------------------*/

    private fun saveProfile(ctx: Context, response: ResponseBody) {
        try {
            val profile = JSONObject(response.string()).getJSONArray("Profile").getJSONObject(0)
            ctx.savePref(profile.getString("ACAD_PROG_DESCR"), R.string.major)
            ctx.savePref(profile.getString("ACAD_CAREER_DESCR"), R.string.degree)
            ctx.savePref(profile.getString("BIRTHDATE"), R.string.birthday)
            ctx.savePref(profile.getString("NAMA"), R.string.name)
            ctx.savePref(profile.getString("NIM"), R.string.nim)
        } catch (e: JSONException) {
            Crashlytics.log(response.string())
            Crashlytics.logException(e)
            throw e
        }
    }

    /**---------------------------------------------------------------------------------------------
     * Save and extract total charge and total payment to preference from server response
     *--------------------------------------------------------------------------------------------*/

    private fun saveFinanceSummary(ctx: Context, response: ResponseBody) {
        try {
            val responseJson = JSONArray(response.string())
            if (responseJson.length() == 0) return
            val summary = responseJson.getJSONObject(0)
            ctx.savePref(summary.getInt("charge"), R.string.finance_charge)
            ctx.savePref(summary.getInt("payment"), R.string.finance_payment)
        } catch (e: JSONException) {
            Crashlytics.log(response.string())
            Crashlytics.logException(e)
        }
    }

    /**---------------------------------------------------------------------------------------------
     * Delete all grades from realm and insert new ones to realm
     *--------------------------------------------------------------------------------------------*/

    private fun Realm.insertGrade(grade: GradeModel) {
        // encode the term into the grade data
        grade.credit.term = grade.term.toInt()
        cleanInsert(grade.gradings)
        insert(grade.scores)
        insertOrUpdate(grade.credit)
    }

    /**---------------------------------------------------------------------------------------------
     * Delete all data before inserting new data
     *--------------------------------------------------------------------------------------------*/

    private fun Realm.cleanInsert(data: List<RealmObject>) {
        if (data.isEmpty()) return
        delete(data[0]::class.java)
        insert(data)
    }

    /**---------------------------------------------------------------------------------------------
     * Build retrofit service for making API Calls
     *--------------------------------------------------------------------------------------------*/

    private fun buildRetrofit() = Retrofit.Builder()
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(if (BuildConfig.DEBUG) buildDebugClient() else buildClient())
            .baseUrl(baseUrl)
            .build().create(DataService::class.java)

    /**---------------------------------------------------------------------------------------------
     * Build OkHttpClient WITH Stheto for DEBUG
     *--------------------------------------------------------------------------------------------*/

    private fun buildDebugClient() = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .followRedirects(false)
            .build()

    /**---------------------------------------------------------------------------------------------
     * Build OkHttpClient WITHOUT Stheto for PRODUCTION
     *--------------------------------------------------------------------------------------------*/

    private fun buildClient() = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .followRedirects(false)
            .build()

    private fun setLastUpdate(ctx: Context) =
            ctx.savePref(DateTime.now().toString(), R.string.last_update)

    fun decideCauseOfFailure(it: Throwable): String {
        Crashlytics.logException(it)
        return when (it) {
            is SocketTimeoutException -> "Request Timed Out"
            is HttpException -> {
                Crashlytics.log("HttpException")
                Crashlytics.logException(it)
                "Binusmaya's server seems to be offline, try again later"
            }
            is ConnectException -> "Failed to connect to Binusmaya"
            is SSLException -> "Failed to connect to Binusmaya"
            is UnknownHostException -> "Failed to connect to Binusmaya"
            is IOException -> "Auth fails, maybe wrong pass, username, or captcha?"
            is NoSuchMethodException -> "Captcha cancelled"
            is IndexOutOfBoundsException -> {
                Crashlytics.log("IndexOutOfBoundsException")
                Crashlytics.logException(it)
                "Binusmaya server is acting weird, try again later"
            }
            else -> {
                Crashlytics.log("Unknown CrashOnSignIn")
                Crashlytics.logException(it)
                "We have no idea what went wrong, but we have received the error log, we'll look into this"
            }
        }
    }
}
