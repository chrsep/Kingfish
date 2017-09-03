package com.directdev.portal.features.grades

import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.GradesInteractor
import com.directdev.portal.utils.generateMessage
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class GradesPresenter @Inject constructor(
        private val gradesInteractor: GradesInteractor,
        private val view: GradesContract.View,
        private val authInteractor: AuthInteractor
) : GradesContract.Presenter {

    private var isSyncing = false
    private var isStopped = false

    override fun switchTerm(term: Int) {
        val credits = if (term == -1) gradesInteractor.getTermCreditAndGpa()
        else gradesInteractor.getTermCreditAndGpa(term)

        val grades = if (term == -1) gradesInteractor.getAllGradesByTerm()
        else gradesInteractor.getAllGradesByTerm(term)

        view.setGpaGraphData(credits)
        if (grades.isEmpty()) {
            view.hideGradesRecycler()
            return
        }
        view.showGradesRecycler()
        view.updateRecycler(grades, credits.first())
    }

    override fun onResume() {
        switchTerm()
        sync()
    }

    override fun onStop() {
        isStopped = true
    }

    override fun onStart() {
        isStopped = false
    }

    override fun setToolbarTitle(firstTerm: Int, chosenTerm: Int) {
        val year = ((chosenTerm + 99) / 100) - ((firstTerm + 99) / 100)
        val title = "Semester " + when (chosenTerm.toString().substring(2)) {
            "10" -> ((year * 2) + 1).toString()
            "20" -> ((year * 2) + 2).toString()
            "30" -> ((year * 2) + 2).toString() + " ( SP )"
            else -> "N/A"
        }
        view.setToolbarTitle(title)
    }

    override fun sync(bypass: Boolean) {
        if (!bypass and (isSyncing or !gradesInteractor.isSyncOverdue())) return
        authInteractor.execute().flatMap {
            gradesInteractor.sync(it)
        }.doOnSubscribe {
            if (!isStopped) view.showLoading()
            isSyncing = true
        }.doFinally {
            if (!isStopped) view.hideLoading()
            isSyncing = false
        }.subscribe({
            view.showSuccess("Journal & Finance updated")
        }, {
            view.showFailed(it.generateMessage())
        })
    }
}