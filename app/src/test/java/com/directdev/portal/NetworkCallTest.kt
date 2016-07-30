package com.directdev.portal

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.Test

class NetworkCallTest {
    @Test
    fun profile_call() {


        DateTime.parse("2016-09-28 00:00:00.000", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS"))
//        val moshi = Moshi.Builder().build()
//        val jsonAdapter = moshi.adapter(Profile::class.java)
//        val profile = jsonAdapter.fromJson(json.getJSONArray("Profile").getJSONObject(1).toString())
//        System.out.print(profile.name)
    }
}
