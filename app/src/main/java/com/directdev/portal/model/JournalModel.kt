package com.directdev.portal.model

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
        date = DateTime.parse(id, DateTimeFormat.forPattern(pattern)).toDate()
        return this
    }
}