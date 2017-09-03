package com.directdev.portal.features.grades

import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.CourseInteractor
import com.directdev.portal.interactors.GradesInteractor
import com.directdev.portal.interactors.TermInteractor
import com.directdev.portal.utils.generateMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class GradesPresenter @Inject constructor(
        private val view: GradesContract.View,
        private val gradesInteractor: GradesInteractor,
        private val authInteractor: AuthInteractor,
        private val courseInteractor: CourseInteractor,
        private val termInteractor: TermInteractor
) : GradesContract.Presenter {
    private var isSyncing = false
    private var isStopped = false

    override fun onStart() {
        isStopped = false
    }

    override fun onResume() {
        val credits = gradesInteractor.getTermCreditAndGpa()
        view.setGpaGraphData(credits)
        view.setGraphStyle()
        switchTerm(credits.size)
        sync()
        if (isSyncing) view.showLoading()
    }

    override fun onStop() {
        isStopped = true
    }

    override fun switchTerm(creditIndex: Int) {
        val credits = gradesInteractor.getTermCreditAndGpa()
        if (credits.isEmpty()) {
            view.hideGradesRecycler()
            return
        }
        val term = credits[creditIndex - 1].term
        view.setToolbarTitle(termInteractor.getSemesterName(term))

        val grades = gradesInteractor.getAllGradesByTerm(term)
        if (grades.isEmpty()) {
            view.hideGradesRecycler()
            return
        }
        view.showGradesRecycler()
        view.updateRecycler(grades, credits[creditIndex - 1])
    }

    override fun sync(bypass: Boolean) {
        if (!bypass and (isSyncing or !gradesInteractor.isSyncOverdue())) return
        authInteractor.execute().flatMap {
            courseInteractor.sync(it)
        }.flatMap {
            gradesInteractor.sync(it)
        }.doOnSubscribe {
            if (!isStopped) view.showLoading()
            isSyncing = true
        }.doFinally {
            if (!isStopped) view.hideLoading()
            isSyncing = false
        }.observeOn(AndroidSchedulers.mainThread()).subscribe({
            view.showSuccess("Grades Updated")
            if (!isStopped) {
                val credits = gradesInteractor.getTermCreditAndGpa()
                view.setGpaGraphData(credits)
                view.setGraphStyle()
                switchTerm(credits.size)
            }
        }, {
            view.showFailed(it.generateMessage())
        })
    }
}