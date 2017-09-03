package com.directdev.portal.repositories

import com.directdev.portal.models.CourseModel
import io.realm.Realm
import io.realm.RealmResults
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 9/2/17.
 *------------------------------------------------------------------------------------------------*/
class CourseRepository @Inject constructor(
        @Named("MainThread") private val realm: Realm
) {
    fun getCourses(term: Int): RealmResults<CourseModel> = realm.where(CourseModel::class.java)
            .equalTo("term", term)
            .findAll()

    fun saveCourses(courses: List<CourseModel>) = Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.insertOrUpdate(courses)
        }
    }
}