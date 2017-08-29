package com.directdev.portal.interactors

import com.directdev.portal.models.JournalModel
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.JournalRepository
import com.directdev.portal.repositories.TermRepository
import com.directdev.portal.repositories.TimeStampRepository
import io.realm.RealmResults
import java.util.*
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
class JournalInteractor @Inject constructor(
        private val journalRepo: JournalRepository,
        private val timeStampRepo: TimeStampRepository,
        private val bimayApi: NetworkHelper,
        private val termRepo: TermRepository
) {
    fun getFutureEntry(): RealmResults<JournalModel> =
            journalRepo.getEntryFromDate(timeStampRepo.today())

    fun getEntryByDate(date: Date = timeStampRepo.today()) =
            getFutureEntry().filter { it.date == date }

    fun checkIsHoliday(): String =
            if (haveSession(getEntryByDate())) timeStampRepo.todayString() else "Holiday"

    fun sync(cookie: String) = bimayApi.getJournalEntries(cookie, termRepo.getTerms()).map {
        journalRepo.save(it)
    }

    private fun haveSession(journal: List<JournalModel>) =
            journal.isNotEmpty() && (journal[0].session.size > 0 || journal[0].exam.size > 0)
}