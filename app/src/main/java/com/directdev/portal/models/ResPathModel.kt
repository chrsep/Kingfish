package com.directdev.portal.models

import com.squareup.moshi.Json
import io.realm.RealmObject

/**
 * Created by chris on 9/14/2016.
 */
open class ResPathModel(
        @Json(name = "Title")
        open var title: String = "N/A",
        @Json(name = "courseOutlineReferencesId")
        open var courseOutlineReferencesId: String = "N/A",
        @Json(name = "courseOutlineTopicID")
        open var courseOutlineTopicID: String = "N/A",
        @Json(name = "description")
        open var description: String = "N/A",
        @Json(name = "filename")
        open var filename: String = "N/A",
        @Json(name = "location")
        open var location: String = "N/A",
        @Json(name = "mediaType")
        open var mediaType: String = "N/A",
        @Json(name = "mediaTypeId")
        open var mediaTypeId: String = "N/A",
        @Json(name = "path")
        open var path: String = "N/A",
        @Json(name = "pathid")
        open var pathid: String = "N/A"
) : RealmObject()