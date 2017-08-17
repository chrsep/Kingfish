package com.directdev.portal.models

import com.squareup.moshi.Json

/**
 * Created by chris on 9/14/2016.
 */

open class ResModelIntermidiary(
        @Json(name = "Book")
        open var book: List<ResBookModel> = mutableListOf(),

        @Json(name = "Path")
        open var path: List<ResPathModel> = mutableListOf(),

        @Json(name = "Resources")
        open var resources: List<ResResourcesModel> = mutableListOf(),

        @Json(name = "Url")
        open var url: List<ResUrlModel> = mutableListOf(),

        open var webContent: String = "https://newcontent.binus.ac.id/data_content/",

        open var classNumber : Int = 0
)