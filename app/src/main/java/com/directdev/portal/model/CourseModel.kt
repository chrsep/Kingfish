package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CourseModel(
        @PrimaryKey
        @Json(name = "CLASS_NBR")
        open var classNumber: Int = 0,

        @Json(name = "COURSEID")
        open var courseId: String = "N/A", //3.370

        @Json(name = "COURSENAME")
        open var courseName: String = "N/A", //0.00

        @Json(name = "CRSE_ID")
        open var crseId: String = "N/A", //60

        @Json(name = "SSR_COMPONENT")
        open var ssrComponent: String = "N/A",                    //146
        open var term: Int = 0

) : RealmObject()

