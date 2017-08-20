package com.directdev.portal.network

import javax.inject.Inject

class BimayApi @Inject constructor(val bimayService: BimayService) {
    fun getIndexHtml() = bimayService.getIndexHtml()

    fun getRandomizedFields(cookie: String, serial: String) = bimayService.getSerial(cookie, serial)

    fun authenticate(cookie: String, fieldMap: HashMap<String, String>) = bimayService.signIn2(cookie, fieldMap)
}