package com.directdev.portal.repositories

import com.directdev.portal.models.JournalModel
import io.realm.Realm
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
class JournalRepository @Inject constructor(
        private val realm: Realm
) {
    fun findFutureSchedules(date: Date): RealmResults<JournalModel> = realm.where(JournalModel::class.java)
            .greaterThanOrEqualTo("date", date)
            .findAllSorted("date")
}