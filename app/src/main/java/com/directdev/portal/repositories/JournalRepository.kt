package com.directdev.portal.repositories

import com.directdev.portal.models.ExamModel
import com.directdev.portal.models.FinanceModel
import com.directdev.portal.models.JournalModel
import com.directdev.portal.models.SessionModel
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
class JournalRepository @Inject constructor(
        @Named("MainThread") private val realm: Realm
) {
    fun getEntryFromDate(date: Date): RealmResults<JournalModel> = realm.where(JournalModel::class.java)
            .greaterThanOrEqualTo("date", date)
            .findAllAsync()
            .sort("date")

    fun save(journals: List<JournalModel>) = Realm.getDefaultInstance().use {
        it.executeTransaction {
            it.delete(JournalModel::class.java)
            it.delete(ExamModel::class.java)
            it.delete(FinanceModel::class.java)
            it.delete(SessionModel::class.java)
            it.insertOrUpdate(journals)
        }
    }


}