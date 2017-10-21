package com.directdev.portal.network

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.directdev.portal.BuildConfig
import com.directdev.portal.R
import com.directdev.portal.models.*
import com.directdev.portal.utils.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.HttpException
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
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

//@Deprecated("User the BimayApi Network Helper")
object DataApi {
    var isActive = false
    private val baseUrl = "https://binusmaya.binus.ac.id/services/ci/index.php/"
    private val api = buildRetrofit()
    private fun isStaff(ctx: Context) = ctx.readPref(R.string.isStaff, false)

    data class RandomTokens(val user: String = "",
                            val pass: String = "",
                            val pair1: Map<String, String> = HashMap<String, String>(),
                            val pair2: Map<String, String> = HashMap<String, String>())

 /*   fun initializeApp(ctx: Context): Single<Unit> {
        val cookie = ctx.readPref(R.string.cookie, "")
        return api.getTerms(cookie).subscribeOnIo().flatMap { terms ->
            Crashlytics.log("initializeApp Term Data: " + terms.map { it.value }.toString())
            Crashlytics.setInt("login_level", 1)
            Crashlytics.setInt("term_size", terms.size)
            val gradeObservable = when (terms.size) {
                1 -> {
                    val grades = fetchGrades(terms, cookie)
                    Crashlytics.setInt("grade_size", grades.size)
                    grades[0].map { arrayOf<Any>(it) }
                }
                0 -> Single.just(arrayOf())
                else -> Single.zip(fetchGrades(terms, cookie)) { grades -> grades }
            }
            Crashlytics.setInt("login_level", 2)
            Single.zip(gradeObservable,
                    api.getProfile(cookie).subscribeOnIo(),
                    fetchCourses(terms, cookie),
                    fetchRecent(ctx, cookie, terms.subList(0, 1), terms.last().value.toString())) {
                grades, profile, courses, _ ->
                Crashlytics.setInt("login_level", 3)
                saveProfile(ctx, profile)
                profile.close()
                val realm = Realm.getDefaultInstance()
                Crashlytics.setInt("login_level", 4)
                realm.executeTransaction {
                    it.insertOrUpdate(terms)
                    it.insertOrUpdate(courses)
                    it.delete(ScoreModel::class.java)
                    grades.forEach { grade -> it.insertGrade(grade as GradeModel) }
                }
                realm.close()
            }
        }.bindToIsActive().doOnSuccess {
            setLastUpdate(ctx)
        }
    }*/


 /*   fun fetchData(ctx: Context): Single<Unit> {
        val cookie = ctx.readPref(R.string.cookie, "")
        val realm = Realm.getDefaultInstance()
        val terms = realm.where(TermModel::class.java).findAllSorted("value").takeLast(3)
        return fetchRecent(ctx, cookie, terms, terms.last().value.toString()).doOnSuccess { _ ->
            setLastUpdate(ctx)
        }.doOnSubscribe {
            isActive = true
        }.doAfterTerminate {
            realm.close()
            isActive = false
        }
    }*/

 /*   private fun fetchRecent(ctx: Context, cookie: String, terms: List<TermModel>, lastTerm: String) = Single.zip(
            api.getFinances(cookie).subscribeOnIo(),
            api.getSessions(cookie).subscribeOnIo(),
            api.getExams(ExamRequestBody(terms.takeLast(2).first().value.toString()), cookie).subscribeOnIo(),
            api.getExams(ExamRequestBody(lastTerm), cookie).subscribeOnIo(),
            api.getGrades(terms.first().value.toString(), cookie).subscribeOnIo(),
            api.getGrades(terms.takeLast(2).first().value.toString(), cookie).subscribeOnIo(),
            api.getGrades(lastTerm, cookie).subscribeOnIo(),
            api.getFinanceSummary(cookie).subscribeOnIo(),
            api.getCourse(lastTerm, cookie).subscribeOnIo(),
            { finance, session, exam1, exam2, grade1, grade2, grade3, financeSummary, course ->
                val realm = Realm.getDefaultInstance()
                realm.executeTransaction {
                    it.delete(JournalModel::class.java)
                    it.delete(ExamModel::class.java)
                    it.delete(FinanceModel::class.java)
                    it.delete(SessionModel::class.java)
                    it.insertOrUpdate(mapToJournal(exam1 + exam2, finance, session))
                    it.insertGrade(grade1)
                    it.insertGrade(grade2)
                    it.insertGrade(grade3)
                    saveFinanceSummary(ctx, financeSummary)
                    saveCourse(course, lastTerm, it)
                }
                realm.close()
                isActive = false
            }).defaultThreads()*/

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

    private fun fetchGrades(terms: List<TermModel>, cookie: String) = terms.map {
        api.getGrades(it.value.toString(), cookie).subscribeOn(Schedulers.io())
    }

    private fun fetchCourses(terms: List<TermModel>, cookie: String) = when (terms.size) {
        1 -> api.getCourse(terms[0].value.toString(), cookie)
                .subscribeOnIo()
                .map {
                    it.courses.forEach { it.term = terms[0].value }
                    it.courses
                }

        else -> Single.zip(terms.drop(1).map { term ->
            api.getCourse(term.value.toString(), cookie)
                    .subscribeOnIo()
                    .map {
                        it.courses.forEach { it.term = term.value }
                        it.courses
                    }
        }) {
            it.filterIsInstance<List<CourseModel>>().flatten()
        }
    }

    fun signIn(ctx: Context, tokens: RandomTokens): Single<out Any> {
        val usernamePair = HashMap<String, String>()
        val passPair = HashMap<String, String>()
        usernamePair.put(tokens.user, ctx.readPref(R.string.username, ""))
        passPair.put(tokens.pass, ctx.readPref(R.string.password, ""))

        val single = if (!DateTime.now().closeToLastUpdate(ctx))
            api.signIn(ctx.readPref(R.string.cookie), usernamePair, passPair, tokens.pair1, tokens.pair2).map {
                Crashlytics.setString("returned_header", it.headers().get("Location") ?: "none")
                Crashlytics.setString("user_field", tokens.user)
                Crashlytics.setString("pass_field", tokens.pass)
                Crashlytics.setString("pair1", tokens.pair1.toString())
                Crashlytics.setString("pair2", tokens.pair2.toString())
                if (isStaff(ctx)) api.switchRole(ctx.readPref(R.string.cookie))
                else Single.just(it)
            } else Single.just("")
        return single.defaultThreads()
    }


    fun getTokens(ctx: Context): Single<RandomTokens> {
        val loaderPattern = "<script src=\".*login/loader.*\""
        val usernamePattern = "<input type=\"text\" name=\".*placeholder=\"Username\""
        val passPattern = "<input type=\"password\" name=\".*placeholder=\"Password\""
        var loaderStr: String
        var userStr: String = ""
        var passStr: String = ""
        var cookie: String = ""
        if (DateTime.now().closeToLastUpdate(ctx)) return Single.just(RandomTokens())
        return api.getIndexHtml().flatMap {
            val body = it.body()?.string() ?: ""
            val loader = Regex(loaderPattern).find(body)?.value ?: ""
            val user = Regex(usernamePattern).find(body)?.value ?: ""
            val pass = Regex(passPattern).find(body)?.value ?: ""
            loaderStr = loader.substring(40, loader.length - 1).removeHtmlEncoding()
            userStr = user.substring(25, user.length - 45).removeHtmlEncoding()
            passStr = pass.substring(29, pass.length - 24).removeHtmlEncoding()
            cookie = it.headers().get("Set-Cookie") ?: ""
            api.getSerial(cookie, loaderStr)
        }.map {
            val pattern = "<input type=\"hidden\" name=\".*\" value=\".*\" />"
            val body = it.body()?.string() ?: ""
            val extraInputs = Regex(pattern).findAll(body).toList()
            val fields = extraInputs[0].value.split(" ")
            val pair1 = HashMap<String, String>()
            val pair2 = HashMap<String, String>()
            pair1.put(fields[2].substring(6, fields[2].length - 1).removeHtmlEncoding(),
                    fields[3].substring(6, fields[3].length - 1).removeHtmlEncoding())
            pair2.put(fields[6].substring(6, fields[6].length - 1).removeHtmlEncoding(),
                    fields[7].substring(6, fields[7].length - 1).removeHtmlEncoding())
            ctx.savePref(R.string.cookie, cookie)
            RandomTokens(userStr, passStr, pair1, pair2)
        }.defaultThreads()
    }


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
            ctx.savePref(R.string.major, profile.getString("ACAD_PROG_DESCR"))
            ctx.savePref(R.string.degree, profile.getString("ACAD_CAREER_DESCR"))
            ctx.savePref(R.string.birthday, profile.getString("BIRTHDATE"))
            ctx.savePref(R.string.name, profile.getString("NAMA"))
            ctx.savePref(R.string.nim, profile.getString("NIM"))
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
     * Replace html encoding in randomized data to normal string
     *--------------------------------------------------------------------------------------------*/

    private fun String.removeHtmlEncoding() = replace("%2F", "/").replace("%3D", "=")


    private fun <T> Single<T>.bindToIsActive() = doOnSubscribe {
        isActive = true
    }.doAfterTerminate {
        isActive = false
    }

    /**---------------------------------------------------------------------------------------------
     * Build retrofit service for making API Calls
     *--------------------------------------------------------------------------------------------*/

    private fun buildRetrofit() = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullConverterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(if (BuildConfig.DEBUG) buildDebugClient() else buildClient())
            .baseUrl(baseUrl)
            .build().create(BimayService::class.java)

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
            ctx.savePref(R.string.last_update, DateTime.now().toString())

    private fun DateTime.closeToLastUpdate(ctx: Context): Boolean {
        val lastUpdate = DateTime.parse(ctx.readPref(R.string.last_update, "2007-07-18T20:25:58.941+07:00"))
        val bool = minusMinutes(10).isBefore(lastUpdate)
        return bool
    }

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
            is IOException -> "Failed to authenticate with Bimay, wrong pass/username?"
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
