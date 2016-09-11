package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CreditModel(
        @PrimaryKey
        open var term: Int = 0,

        @Json(name = "GPA_CUR")
        open var gpaCurrent: String = "N/A", //3.370

        @Json(name = "GPA_CUM")
        open var gpaCummulative: String = "N/A", //0.00

        @Json(name = "SCU_CUR")
        open var scuFinished: Int = 0, //60

        @Json(name = "SCU_MAX")
        open var scuMax: Int = 0                    //146
) : RealmObject()
