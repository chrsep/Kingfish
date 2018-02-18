package com.directdev.portal.repositories

import android.util.Log
import com.directdev.portal.models.ResBookModel
import com.directdev.portal.models.ResModel
import com.directdev.portal.models.ResModelIntermidiary
import io.realm.Realm
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 10/21/17.
 *------------------------------------------------------------------------------------------------*/
class ResourceRepository @Inject constructor(
        private val realm: Realm
) {
    fun getResources(classNumber: Int): ResModel? = realm.where(ResModel::class.java)
            .equalTo("classNumber", classNumber)
            .findFirst()

    fun save(data: ResModelIntermidiary) = Realm.getDefaultInstance().use {
        it.executeTransaction { realm ->
            val resModel = ResModel()
            resModel.book.addAll(data.book)
            resModel.path.addAll(data.path)
            resModel.resources.addAll(data.resources)
            resModel.url.addAll(data.url)
            resModel.webContent = data.webContent
            resModel.classNumber = data.classNumber
            realm.insertOrUpdate(resModel)
        }
    }
}