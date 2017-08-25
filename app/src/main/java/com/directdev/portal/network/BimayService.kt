package com.directdev.portal.network

import com.directdev.portal.BuildConfig
import com.directdev.portal.models.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**-------------------------------------------------------------------------------------------------
 * Interface for building Retrofit service.
 *------------------------------------------------------------------------------------------------*/

interface BimayService {

    @GET("https://binusmaya.binus.ac.id/login/index.php")
    @Headers("User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    fun getIndexHtml(@Header("Cookie") cookie: String = ""): Single<Response<ResponseBody>>

    @FormUrlEncoded
    @Headers("Referer: https://binusmaya.binus.ac.id/login/",
            "Origin: https://binusmaya.binus.ac.id",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("https://binusmaya.binus.ac.id/login/sys_login.php")
    fun signIn2(@Header("Cookie") cookie: String,
               @FieldMap fields: Map<String, String>,
               @Field("ctl00\$ContentPlaceHolder1\$SubmitButtonBM") button: String = "Login")
            : Single<Response<String>>

    @FormUrlEncoded
    @Headers("Referer: https://binusmaya.binus.ac.id/login/",
            "Origin: https://binusmaya.binus.ac.id",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("https://binusmaya.binus.ac.id/login/sys_login.php")
    fun signIn(@Header("Cookie") cookie: String,
               @FieldMap uid: Map<String, String>,
               @FieldMap pass: Map<String, String>,
               @FieldMap extraPair1: Map<String, String>,
               @FieldMap extraPair2: Map<String, String>,
               @Field("ctl00\$ContentPlaceHolder1\$SubmitButtonBM") button: String = "Login")
            : Single<Response<String>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStaff/",
            "Origin: https://binusmaya.binus.ac.id",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("https://binusmaya.binus.ac.id/services/ci/index.php/login/switchrole/2/104")
    fun switchRole(@Header("Cookie") cookie: String): Single<Response<ResponseBody>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/",
            "User-Agent: Portal/"+BuildConfig.VERSION_NAME)
    @GET("student/profile/profileStudent")
    fun getProfile(@Header("Cookie") cookie: String): Single<ResponseBody>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("financial/virtualaccount/")
    fun getFinanceSummary(@Header("Cookie") cookie: String): Single<ResponseBody>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("financial/getFinancialSummary")
    fun getFinances(@Header("Cookie") cookie: String): Single<List<FinanceModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newStudent/index.html",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("student/class_schedule/classScheduleGetStudentClassSchedule")
    fun getSessions(@Header("Cookie") cookie: String): Single<List<SessionModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("newExam/Schedule/getOwnScheduleStudent")
    fun getExams(@Body data: ExamRequestBody, @Header("Cookie") cookie: String): Single<List<ExamModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("scoring/ViewGrade/getStudentScore/{term}")
    fun getGrades(@Path("term") term: String, @Header("Cookie") cookie: String): Single<GradeModel>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @POST("https://binusmaya.binus.ac.id/services/ci/index.php/scoring/ViewGrade/getPeriodByBinusianId")
    fun getTerms(@Header("Cookie") cookie: String): Single<List<TermModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("student/init/getCoursesBySTRMAndAcad/{term}")
    fun getCourse(@Path("term") term: String, @Header("Cookie") cookie: String): Single<CourseWrapperModel>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("student/classes/resources/{courseId}/{crseId}/{term}/{ssrComponent}/{classNumber}")
    fun getResources(@Path("courseId") courseId: String,
                     @Path("crseId") crseId: String,
                     @Path("term") term: String,
                     @Path("ssrComponent") ssrComponent: String,
                     @Path("classNumber") classNumber: String,
                     @Header("Cookie") cookie: String): Single<ResModelIntermidiary>

    @Headers("Referer: https://binusmaya.binus.ac.id/newstudent/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("student/classes/assignmentType/{courseId}/{crseId}/{term}/{ssrComponent}/{classNumber}/01")
    fun getAssignment(@Path("courseId") courseId: String,
                      @Path("crseId") crseId: String,
                      @Path("term") term: String,
                      @Path("ssrComponent") ssrComponent: String,
                      @Path("classNumber") classNumber: String,
                      @Header("Cookie") cookie: String): Single<List<AssignmentIndividualModel>>

    @Headers("Referer: https://binusmaya.binus.ac.id/login/",
            "User-Agent: Portal App/"+BuildConfig.VERSION_NAME)
    @GET("https://binusmaya.binus.ac.id/login/loader.php")
    fun getSerial(@Header("Cookie") cookie: String,
                  @Query("serial") serial: String): Single<Response<ResponseBody>>
}
