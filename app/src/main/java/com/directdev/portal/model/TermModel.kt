package com.directdev.portal.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TermModel(
        @PrimaryKey
        open var value: Int = 0
) : RealmObject()
