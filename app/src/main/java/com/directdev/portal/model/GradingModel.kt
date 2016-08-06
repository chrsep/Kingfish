package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

open class GradingModel(
        @Json(name = "descr")
        open var point: Float = 0f, //4

        open var grade: String = "N/A", //A

        open var range: String = "N/A"      //90 - 100
) : RealmObject()
