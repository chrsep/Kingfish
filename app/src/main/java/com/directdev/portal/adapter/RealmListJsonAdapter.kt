package com.directdev.portal.adapter

import com.directdev.portal.model.ResBookModel
import com.directdev.portal.model.ResPathModel
import com.directdev.portal.model.ResResourcesModel
import com.directdev.portal.model.ResUrlModel
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import io.realm.RealmList

/**
 * Created by chris on 9/14/2016.
 */
class RealmListJsonAdapter() {
    @FromJson fun <T : ResBookModel> eventFromJson(json: List<T>): RealmList<T> {
        val list = RealmList<T>()
        list.addAll(json)
        return list
    }

    @ToJson fun <T : ResBookModel> eventToJson(list: RealmList<out T>): List<T> {
        return list.toList()
    }


}