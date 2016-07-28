package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

open class ProfileModel : RealmObject() {
    @Json(name = "Profile.NAMA")
    open var name: String? = "Sushi"
}