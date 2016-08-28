package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

open class SessionModel(

        @Json(name = "CLASS_SECTION")
        open var classId: String = "N/A", //"LB02"

        @Json(name = "COURSE_TITLE_LONG")
        open var courseName: String = "N/A", //"Character Building: Agama"

        @Json(name = "CRSE_CODE")
        open var courseId: String = "N/A", //""COMP6060""

        @Json(name = "START_DT")
        open var date: String = "N/A", //""2014-09-23 00:00:00.000""

        @Json(name = "LOCATION")
        open var locationId: String = "N/A", //""COMP6060""

        @Json(name = "LOCATION_DESCR")
        open var locationName: String = "N/A", //""COMP6060""

        @Json(name = "MEETING_TIME_START")
        open var startTime: String = "N/A", //""COMP6060""

        @Json(name = "MEETING_TIME_END")
        open var endTime: String = "N/A", //""COMP6060""

        @Json(name = "N_DELIVERY_MODE")
        open var deliveryMode: String = "N/A", //""COMP6060""

        @Json(name = "N_WEEK_SESSION")
        open var weekCount: String = "N/A", //""COMP6060""

        @Json(name = "ROOM")
        open var room: String = "N/A", //""COMP6060""

        @Json(name = "SSR_COMPONENT")
        open var typeId: String = "N/A", //""COMP6060""

        @Json(name = "SSR_DESCR")
        open var typeName: String = "N/A", //""COMP6060""

        @Json(name = "SessionIDNum")
        open var sessionCount: String = "N/A" //""COMP6060""

) : RealmObject()
