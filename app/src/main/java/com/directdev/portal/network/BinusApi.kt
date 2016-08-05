package com.directdev.portal.network

import com.directdev.portal.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable
import rx.Single

interface BinusApi {
    @FormUrlEncoded
    @Headers("Referer: https://newbinusmaya.binus.ac.id/login/",
            "Origin: https://newbinusmaya.binus.ac.id")
    @POST("https://newbinusmaya.binus.ac.id/login/sys_login.php")
    fun login(@Field("uid") uid: String, @Field("pass") pass: String, @Field("ctl00\$ContentPlaceHolder1\$SubmitButtonBM") button: String): Single<Response<String>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/",
            "Cookie: PHPSESSID=53epm48lsc141ag4cn2iavune0")
    @GET("student/profile/profileStudent")
    fun getProfile(): Observable<ResponseBody>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/",
            "Cookie: PHPSESSID=53epm48lsc141ag4cn2iavune0")
    @GET("financial/getFinancialSummary")
    fun getFinances(): Observable<List<FinanceModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newStudent/index.html",
            "Cookie: PHPSESSID=53epm48lsc141ag4cn2iavune0")
    @GET("student/class_schedule/classScheduleGetStudentClassSchedule")
    fun getSchedules(): Observable<List<ScheduleModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newstudent/",
            "Cookie: PHPSESSID=53epm48lsc141ag4cn2iavune0")
    @POST("newExam/Schedule/getOwnScheduleStudent")
    fun getExam(@Body data: ExamRequestBody): Observable<List<ExamModel>>

    @Headers("Referer: https://newbinusmaya.binus.ac.id/newstudent/",
            "Cookie: PHPSESSID=53epm48lsc141ag4cn2iavune0")
    @POST("scoring/ViewGrade/getStudentScore/{term}")
    fun getGrades(@Path("term") term: String): Observable<GradeModel>
}
