package com.directdev.portal.network

import javax.inject.Inject



class BimayApi @Inject constructor(override val bimayService: BimayService) : NetworkHelper {
    override fun getIndexHtml() = bimayService.getIndexHtml()

    override fun getRandomizedFields(cookie: String, serial: String) = bimayService.getSerial(cookie, serial)

    override fun authenticate(cookie: String, fieldMap: HashMap<String, String>) = bimayService.signIn2(cookie, fieldMap)
}