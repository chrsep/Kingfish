package com.directdev.portal.models

import com.squareup.moshi.Json
import io.realm.RealmObject

/**
 * Created by chris on 9/14/2016.
 */
open class ResUrlModel(
        @Json(name = "courseOutlineTopicID")
        open var courseOutlineTopicID: String = "N/A",

        @Json(name = "courseOutlineReferencesId")
        open var courseOutlineReferencesId: String = "N/A",

        @Json(name = "mediaTypeId")
        open var mediaTypeId: String = "N/A",

        @Json(name = "mediaType")
        open var mediaType: String = "N/A",

        @Json(name = "Title")
        open var title: String = "N/A",

        @Json(name = "description")
        open var description: String = "N/A",


        @Json(name = "urlID")
        open var urlID: String = "N/A",

        @Json(name = "url")
        open var url: String = "N/A"
) : RealmObject()