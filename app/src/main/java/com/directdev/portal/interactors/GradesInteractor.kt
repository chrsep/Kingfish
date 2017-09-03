package com.directdev.portal.interactors

import com.directdev.portal.models.CreditModel
import com.directdev.portal.models.GradeModel
import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.CourseRepository
import com.directdev.portal.repositories.GradesRepository
import com.directdev.portal.repositories.TermRepository
import com.directdev.portal.repositories.TimeStampRepository
import io.realm.RealmResults
import org.joda.time.Minutes
import javax.inject.Inject
import javax.inject.Named

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class GradesInteractor @Inject constructor(
        private val gradesRepo: GradesRepository,
        private val bimayApi: NetworkHelper,
        private val courseRepo: CourseRepository,
        private val termRepo: TermRepository,
        @Named("grade") private val timeStampRepo: TimeStampRepository
) {
    fun getTermCreditAndGpa(term: Int = termRepo.getLatestTerm()): RealmResults<CreditModel> =
            gradesRepo.getCreditAndGpa(term)

    fun getAllGradesByTerm(term: Int = termRepo.getLatestTerm()) = courseRepo.getCourses(term).map {
        gradesRepo.getGrades(it.courseId)
    }.filter {
        it.isNotEmpty()
    }

    fun sync(cookie: String) = bimayApi.getGrades(cookie, termRepo.getTerms()).map {
        it.forEach { gradesRepo.saveGrades(it as GradeModel) }
    }.doOnSuccess {
        timeStampRepo.updateLastSyncDate()
    }

    fun isSyncOverdue(): Boolean {
        val minutesInt = Minutes.minutesBetween(timeStampRepo.getLastSync(), timeStampRepo.today()).minutes
        return Math.abs(minutesInt) > 5
    }
}