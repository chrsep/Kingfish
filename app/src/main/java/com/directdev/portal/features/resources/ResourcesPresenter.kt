package com.directdev.portal.features.resources

import com.directdev.portal.interactors.CourseInteractor
import com.directdev.portal.interactors.TermInteractor
import javax.inject.Inject

/**
 * Created by chris on 04/10/17.
 */
class ResourcesPresenter @Inject constructor(
        private val termInteractor: TermInteractor,
        private val courseInteractor: CourseInteractor
) : ResourcesContract.Presenter {
    override fun getSemesters(): List<String> = termInteractor.getTerms().map {
        termInteractor.getSemesterName(it)
    }

    override fun updateSelectedSemester(selectedTerm: String) {
    }

    override fun getCourses(term: String): List<String> = mutableListOf()

}