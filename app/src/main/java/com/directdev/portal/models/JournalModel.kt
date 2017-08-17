package com.directdev.portal.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

open class JournalModel(
        @PrimaryKey
        open var id: String = "N/A",
        open var date: Date = Date(),
        open var session: RealmList<SessionModel> = RealmList(),
        open var finance: RealmList<FinanceModel> = RealmList(),
        open var exam: RealmList<ExamModel> = RealmList()
) : RealmObject() {
    fun setDate(pattern: String = "yyyy-MM-dd HH:mm:ss.SSS"): JournalModel {
        try {
            date = DateTime.parse(id, DateTimeFormat.forPattern(pattern)).toDate()
        } catch(e: Exception) {
            date = DateTime.parse(id, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS")).toDate()
        }
        return this
    }
}