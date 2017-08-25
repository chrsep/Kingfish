package com.directdev.portal.network

import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class BimayApi @Inject constructor(override val bimayService: BimayService) : NetworkHelper {
    override fun getIndexHtml() =
            bimayService.getIndexHtml().subscribeOn(Schedulers.io())

    override fun getRandomizedFields(cookie: String, serial: String) =
            bimayService.getSerial(cookie, serial).subscribeOn(Schedulers.io())

    override fun authenticate(cookie: String, fieldMap: HashMap<String, String>) =
            bimayService.signIn2(cookie, fieldMap).subscribeOn(Schedulers.io())

    override fun switchRole(cookie: String) = bimayService.switchRole(cookie)
}