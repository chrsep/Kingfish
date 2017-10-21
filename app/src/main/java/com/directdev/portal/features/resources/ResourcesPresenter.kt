package com.directdev.portal.features.resources

import com.directdev.portal.interactors.CourseInteractor
import com.directdev.portal.interactors.TermInteractor
import com.directdev.portal.utils.getInitials
import javax.inject.Inject

/**
 * Created by chris on 04/10/17.
 */
class ResourcesPresenter @Inject constructor(
        private val termInteractor: TermInteractor,
        private val courseInteractor: CourseInteractor,
        private val view: ResourcesContract.View
) : ResourcesContract.Presenter {
    override fun getSemesters(): List<Pair<Int, String>> {
        val terms = termInteractor.getTerms()
        val semesterNames = terms.map {
            termInteractor.getSemesterName(it)
        }
        return terms.zip(semesterNames)
    }

    override fun updateSelectedSemester(selectedTerm: Int) {
        val courses = courseInteractor.getCourses(selectedTerm)
        view.updateCourses(courses.map { Pair(it.first.getInitials(), it.second) })
    }

    override fun getCourses(term: String): List<String> = mutableListOf()

}