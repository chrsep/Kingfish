package com.directdev.portal.network

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject


class BimayApi @Inject constructor(override val bimayService: BimayService) : NetworkHelper {
    override fun getIndexHtml(): Single<Response<ResponseBody>> =
            bimayService.getIndexHtml().subscribeOn(Schedulers.io())

    override fun getRandomizedFields(cookie: String, serial: String): Single<Response<ResponseBody>> =
            bimayService.getSerial(cookie, serial).subscribeOn(Schedulers.io())

    override fun authenticate(cookie: String, fieldMap: HashMap<String, String>): Single<Response<String>> =
            bimayService.signIn2(cookie, fieldMap).subscribeOn(Schedulers.io())

    override fun switchRole(cookie: String) = bimayService.switchRole(cookie)

    override fun getUserProfile(cookie: String): Single<ResponseBody> =
            bimayService.getProfile(cookie).subscribeOn(Schedulers.io())
}