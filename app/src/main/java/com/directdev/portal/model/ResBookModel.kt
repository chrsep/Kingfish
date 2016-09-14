package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

/**
 * Created by chris on 9/14/2016.
 */
open class ResBookModel(
        @Json(name = "ISBN")
        open var isbn: String = "N/A",
        @Json(name = "Type")
        open var type: String = "N/A",
        @Json(name = "Year")
        open var year: String = "N/A",
        //@Json(name = "author")
        open var author: String = "N/A",
        //@Json(name = "bibli")
        open var bibli: String = "N/A",
        //@Json(name = "city")
        open var city: String = "N/A",
        //@Json(name = "courseOutlineTopicID")
        open var courseOutlineTopicID: String = "N/A",
        @Json(name = "desc")
        open var description: String = "N/A",
        //@Json(name = "edition")
        open var edition: String = "N/A",
        //@Json(name = "publisher")
        open var publisher: String = "N/A",
        //@Json(name = "title")
        open var title: String = "N/A"
) : RealmObject()