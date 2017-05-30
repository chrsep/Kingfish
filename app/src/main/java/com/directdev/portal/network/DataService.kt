package com.directdev.portal.network

import com.directdev.portal.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Single

/**-------------------------------------------------------------------------------------------------
 * Interface for building Retrofit service.
 *------------------------------------------------------------------------------------------------*/

interface DataService {

    @GET("https://binusmaya.binus.ac.id/login/index.php")
    fun getToken(@Header("Cookie") cookie: String): Single<Response<ResponseBody>>

    @FormUrlEncoded
    @Headers("Referer: https://binusmaya.binus.ac.id/login/", "Origin: https://binusmaya.binus.ac.id")
    @POST("https://binusmaya.binus.ac.id/login/sys_login.php")
    fun signIn(@Field("uid") uid: String,
               @Field("pass") pass: String,
               @Header("Cookie") cookie: String,
               @Field("defaultLoginReal") defaultLoginReal: String,
               @Field("token") token: String? = "",
               @Field("ctl00\$ContentPlaceHolder1\$SubmitButtonBM") button: String = "Login")
            : Single<Response<String>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStaff/", "Origin: https://binusmaya.binus.ac.id")
    @POST("https://binusmaya.binus.ac.id/services/ci/index.php/login/switchrole/2/104")
    fun switchRole(@Header("Cookie") cookie: String): Single<Response<ResponseBody>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/")
    @GET("student/profile/profileStudent")
    fun getProfile(@Header("Cookie") cookie: String): Single<ResponseBody>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/")
    @GET("financial/virtualaccount/")
    fun getFinanceSummary(@Header("Cookie") cookie: String): Single<ResponseBody>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/")
    @GET("financial/getFinancialSummary")
    fun getFinances(@Header("Cookie") cookie: String): Single<List<FinanceModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/index.html")
    @GET("student/class_schedule/classScheduleGetStudentClassSchedule")
    fun getSessions(@Header("Cookie") cookie: String): Single<List<SessionModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @POST("newExam/Schedule/getOwnScheduleStudent")
    fun getExams(@Body data: ExamRequestBody, @Header("Cookie") cookie: String): Single<List<ExamModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @POST("scoring/ViewGrade/getStudentScore/{term}")
    fun getGrades(@Path("term") term: String, @Header("Cookie") cookie: String): Single<GradeModel>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @POST("https://binusmaya.binus.ac.id/services/ci/index.php/scoring/ViewGrade/getPeriodByBinusianId")
    fun getTerms(@Header("Cookie") cookie: String): Single<List<TermModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @GET("student/init/getCoursesBySTRMAndAcad/{term}")
    fun getCourse(@Path("term") term: String, @Header("Cookie") cookie: String): Single<CourseWrapperModel>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @GET("student/classes/resources/{courseId}/{crseId}/{term}/{ssrComponent}/{classNumber}")
    fun getResources(@Path("courseId") courseId: String,
                     @Path("crseId") crseId: String,
                     @Path("term") term: String,
                     @Path("ssrComponent") ssrComponent: String,
                     @Path("classNumber") classNumber: String,
                     @Header("Cookie") cookie: String): Single<ResModelIntermidiary>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/")
    @GET("student/classes/assignmentType/{courseId}/{crseId}/{term}/{ssrComponent}/{classNumber}/01")
    fun getAssignment(@Path("courseId") courseId: String,
                      @Path("crseId") crseId: String,
                      @Path("term") term: String,
                      @Path("ssrComponent") ssrComponent: String,
                      @Path("classNumber") classNumber: String,
                      @Header("Cookie") cookie: String): Single<List<AssignmentIndividualModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/login/")
    @GET("https://binusmaya.binus.ac.id/login/captcha.php")
    fun getCaptchaImage(@Header("Cookie") cookie: String): Single<Response<ResponseBody>>
}
