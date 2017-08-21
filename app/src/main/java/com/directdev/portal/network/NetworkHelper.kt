package com.directdev.portal.network

import okhttp3.ResponseBody
import retrofit2.Response
import rx.Single

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/21/17.
 *------------------------------------------------------------------------------------------------*/
interface NetworkHelper {
    val bimayService: BimayService
    fun getIndexHtml(): Single<Response<ResponseBody>>
    fun getRandomizedFields(cookie: String, serial: String): Single<Response<ResponseBody>>
    fun authenticate(cookie: String, fieldMap: HashMap<String, String>): Single<Response<String>>
}