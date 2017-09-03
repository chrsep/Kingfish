package com.directdev.portal.interactors

import com.directdev.portal.models.JournalModel
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.JournalRepository
import com.directdev.portal.repositories.TermRepository
import com.directdev.portal.repositories.TimeStampRepository
import io.reactivex.Single
import io.realm.RealmResults
import org.joda.time.Minutes
import java.util.*
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
class JournalInteractor @Inject constructor(
        private val journalRepo: JournalRepository,
        private val bimayApi: NetworkHelper,
        private val termRepo: TermRepository,
        @Named("journal") private val timeStampRepo: TimeStampRepository
) {
    fun getFutureEntry(): RealmResults<JournalModel> =
            journalRepo.getEntryFromDate(timeStampRepo.today().withTimeAtStartOfDay().toDate())

    fun getEntryByDate(date: Date = timeStampRepo.today().withTimeAtStartOfDay().toDate()) =
            getFutureEntry().filter { it.date == date }

    fun checkIsHoliday(): String =
            if (haveSession(getEntryByDate())) timeStampRepo.todayString() else "Holiday"

    fun sync(cookie: String): Single<Unit> = bimayApi.getJournalEntries(cookie, termRepo.getTerms()).map {
        journalRepo.save(it)
    }.doOnSuccess {
        timeStampRepo.updateLastSyncDate()
    }

    private fun haveSession(journal: List<JournalModel>) =
            journal.isNotEmpty() && (journal[0].session.size > 0 || journal[0].exam.size > 0)

    fun isSyncOverdue(): Boolean {
        val minutesInt = Minutes.minutesBetween(timeStampRepo.getLastSync(), timeStampRepo.today()).minutes
        return Math.abs(minutesInt) > 5
    }
}