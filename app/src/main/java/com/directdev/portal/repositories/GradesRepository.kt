package com.directdev.portal.repositories

import com.directdev.portal.models.CreditModel
import com.directdev.portal.models.GradeModel
import com.directdev.portal.models.ScoreModel
import io.realm.Realm
import io.realm.RealmResults
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class GradesRepository @Inject constructor(
        private val realm: Realm
) {
    fun getGrades(courseId: String): RealmResults<ScoreModel> = realm.where(ScoreModel::class.java)
            .equalTo("courseId", courseId)
            .findAll()

    fun getCreditAndGpa(term: Int): RealmResults<CreditModel> = realm.where(CreditModel::class.java)
            .findAllSorted("term")

    fun saveGrades(grade: GradeModel) = Realm.getDefaultInstance().use {
        it.executeTransaction {
            grade.credit.term = grade.term.toInt()
            if (!grade.gradings.isEmpty()) {
                it.delete(grade.gradings[0]::class.java)
                it.insert(grade.gradings)
            }
            it.insert(grade.scores)
            it.insertOrUpdate(grade.credit)
        }
    }

}