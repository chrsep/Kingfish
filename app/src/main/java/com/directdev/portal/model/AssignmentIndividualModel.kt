package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

/**
 * Created by chris on 9/14/2016.
 */
open class AssignmentIndividualModel(
        open var classNumber: Int = 0,
        @Json(name = "AssignmentFrom")
        open var from: String = "N/A",
        @Json(name = "Date")
        open var date: String = "N/A",
        @Json(name = "StudentAssignmentID")
        open var id: String = "N/A",
        @Json(name = "Title")
        open var title: String = "N/A",
        @Json(name = "assignmentPathLocation")
        open var path: String = "N/A",
        @Json(name = "deadlineDuration")
        open var deadlineDate: String = "N/A",
        @Json(name = "deadlineTime")
        open var deadlineHour: String = "N/A",
        open var webcontent: String = "N/A"
) : RealmObject()