package com.directdev.portal.network

import com.directdev.portal.model.*
import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

interface BinusApi {
    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/",
            "Cookie: PHPSESSID=rii64bhoik5he6lhslh2a93nf6")
    @GET("student/profile/profileStudent")
    fun getProfile(): Observable<ResponseBody>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/",
            "Cookie: PHPSESSID=rii64bhoik5he6lhslh2a93nf6")
    @GET("financial/getFinancialSummary")
    fun getFinances(): Observable<List<FinanceModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/index.html",
            "Cookie: PHPSESSID=rii64bhoik5he6lhslh2a93nf6")
    @GET("student/class_schedule/classScheduleGetStudentClassSchedule")
    fun getSchedules(): Observable<List<ScheduleModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newstudent/",
            "Cookie: PHPSESSID=rii64bhoik5he6lhslh2a93nf6")
    @POST("newExam/Schedule/getOwnScheduleStudent")
    fun getExam(@Body data: ExamRequestBody): Observable<List<ExamModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newstudent/",
            "Cookie: PHPSESSID=rii64bhoik5he6lhslh2a93nf6")
    @POST("scoring/ViewGrade/getStudentScore/{term}")
    fun getGrades(@Path("term") term: String): Observable<GradeModel>
}