package com.directdev.portal.models

import com.squareup.moshi.Json
import io.realm.RealmObject

open class ExamModel(
        @Json(name = "CLASS_SECTION")
        open var classId: String = "N/A", //"LB02"

        @Json(name = "COURSE_TITLE_LONG")
        open var courseName: String = "N/A", //"Character Building: Agama"

        @Json(name = "ChairNumber")
        open var chairNumber: String = "N/A", //"2"

        @Json(name = "DESCR")
        open var examType: String = "N/A", //"Final Exam"

        @Json(name = "Duration")
        open var duration: String = "N/A", //"90"

        @Json(name = "ExamDate")
        open var date: String = "N/A", //"2016-02-05"

        @Json(name = "ExamShift")
        open var shift: String = "N/A", //"08:00 - 09:30"

        @Json(name = "KDMTK")
        open var courseId: String = "N/A", //"CHAR6015"

        @Json(name = "ROOM")
        open var room: String = "N/A", //"ASA1601"

        @Json(name = "elig")
        open var eligibility: String = "N/A"   //"Eligible"
) : RealmObject() {

}