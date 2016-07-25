package com.directdev.portal.realm

import io.realm.RealmObject

open class Cat : RealmObject() {
    open var name: String? = null
}