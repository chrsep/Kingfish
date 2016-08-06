package com.directdev.portal.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class ActivityDate(
        open var date: Date = Date(),
        @PrimaryKey
        open var id: String = "N/A"
) : RealmObject()