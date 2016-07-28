package com.directdev.portal.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import rx.Observable

interface ProfileApi{
    @Headers(
        "Referer: https://newbinusmaya.binus.ac.id/newStudent/",
        "Cookie: _ga=GA1.3.1002681577.1457170902; sada=asdasdasd; PHPSESSID=dah9s9i3f849oh358mij9qb160"
    )
    @GET("/services/ci/index.php/student/profile/profileStudent")
    fun getProfile() : Observable<ResponseBody>

    @Headers(
            "Referer: https://newbinusmaya.binus.ac.id/newStudent/",
            "Cookie: _ga=GA1.3.1002681577.1457170902; sada=asdasdasd; PHPSESSID=dah9s9i3f849oh358mij9qb160"
    )
    @GET("/services/ci/index.php/financial/getFinancialSummary")
    fun getFinance() : Observable<ResponseBody>

    @Headers(
            "Referer: https://newbinusmaya.binus.ac.id/newStudent/index.html",
            "Cookie: _ga=GA1.3.1002681577.1457170902; sada=asdasdasd; PHPSESSID=dah9s9i3f849oh358mij9qb160"
    )
    @GET("/services/ci/index.php/student/class_schedule/classScheduleGetStudentClassSchedule")
    fun getSchedule() : Observable<ResponseBody>
}