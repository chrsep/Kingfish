package com.directdev.portal.models

import com.squareup.moshi.Json
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by chris on 9/14/2016.
 */
open class ResModel(
        @Json(name = "Book")
        open var book: RealmList<ResBookModel> = RealmList(),

        @Json(name = "Path")
        open var path: RealmList<ResPathModel> = RealmList(),

        @Json(name = "Resources")
        open var resources: RealmList<ResResourcesModel> = RealmList(),

        @Json(name = "Url")
        open var url: RealmList<ResUrlModel> = RealmList(),

        open var webContent: String = "https://newcontent.binus.ac.id/data_content/",
        @PrimaryKey
        open var classNumber : Int = 0
) : RealmObject()