package com.directdev.portal.network

import com.directdev.portal.models.CourseModel
import com.directdev.portal.models.ExamModel
import com.directdev.portal.models.JournalModel
import com.directdev.portal.models.TermModel
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/21/17.
 *------------------------------------------------------------------------------------------------*/
interface NetworkHelper {
    fun getIndexHtml(): Single<Response<ResponseBody>>
    fun getLoaderJs(cookie: String, serial: String): Single<Response<ResponseBody>>
    fun authenticate(cookie: String, fieldMap: HashMap<String, String>): Single<Response<String>>
    fun switchRole(cookie: String): Single<ResponseBody>
    fun getUserProfile(cookie: String): Single<ResponseBody>
    fun getJournalEntries(cookie: String, terms: List<Int>): Single<List<JournalModel>>
    fun getExams(cookie: String, terms: List<Int>): Single<List<ExamModel>>
    fun getTerms(cookie: String): Single<List<TermModel>>
    fun getGrades(cookie: String, terms: List<Int>): Single<Array<Any>>
    fun getCourses(cookie: String, terms: List<Int>): Single<List<CourseModel>>
}