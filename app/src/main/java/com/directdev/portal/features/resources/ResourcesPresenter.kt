package com.directdev.portal.features.resources

import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.CourseInteractor
import com.directdev.portal.interactors.ResourceInteractor
import com.directdev.portal.interactors.TermInteractor
import com.directdev.portal.models.ResModel
import javax.inject.Inject

/**
 * Created by chris on 04/10/17.
 */
class ResourcesPresenter @Inject constructor(
        private val termInteractor: TermInteractor,
        private val courseInteractor: CourseInteractor,
        private val resourceInteractor: ResourceInteractor,
        private val authInteractor: AuthInteractor,
        private val view: ResourcesContract.View
) : ResourcesContract.Presenter {
    override fun sync(courseNumber: Int) = authInteractor.execute().flatMap {
        resourceInteractor.sync(it, courseInteractor.getCourse(courseNumber))
    }

    override fun getResources(classNumber: Int): ResModel? =
            resourceInteractor.getResource(classNumber)

    override fun getSemesters(): List<Pair<Int, String>> {
        val terms = termInteractor.getTerms()
        val semesterNames = terms.map {
            termInteractor.getSemesterName(it)
        }
        return terms.zip(semesterNames)
    }

    override fun updateSelectedSemester(selectedTerm: Int) {
        val courses = courseInteractor.getCourses(selectedTerm)
        view.updateCourses(courses.toList())
    }

    override fun getCourses(term: String): List<String> = mutableListOf()

}