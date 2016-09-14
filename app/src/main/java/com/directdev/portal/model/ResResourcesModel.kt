package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

/**
 * Created by chris on 9/14/2016.
 */
open class ResResourcesModel(
        @Json(name = "MeetingDate")
        open var meetingDate: String = "N/A",
        @Json(name = "N_DELIVERY_MODE")
        open var ndeliverymode: String = "N/A",
        @Json(name = "sessionIDNUM")
        open var sessionIDNUM: String = "N/A",
        @Json(name = "CRSE_ID")
        open var crseid: String = "N/A",
        @Json(name = "ACAD_CAREER")
        open var acadcareer: String = "N/A",
        @Json(name = "courseOutlineID")
        open var courseOutlineID: String = "N/A",
        @Json(name = "courseOutlineTopicID")
        open var courseOutlineTopicID: String = "N/A",
        @Json(name = "courseOutlineTopic")
        open var courseOutlineTopic: String = "N/A",
        @Json(name = "courseOutlineSubTopicID")
        open var courseOutlineSubTopicID: String = "N/A",
        @Json(name = "courseOutlineSubTopic")
        open var courseOutlineSubTopic: String = "N/A"
) : RealmObject()