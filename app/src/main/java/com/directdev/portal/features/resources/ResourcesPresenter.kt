package com.directdev.portal.features.resources

import com.directdev.portal.interactors.AuthInteractor
import com.directdev.portal.interactors.CourseInteractor
import com.directdev.portal.interactors.ResourceInteractor
import com.directdev.portal.interactors.TermInteractor
import com.directdev.portal.models.ResModel
import com.directdev.portal.utils.generateMessage
import io.reactivex.Single
import javax.inject.Inject

class ResourcesPresenter @Inject constructor(
        private val termInteractor: TermInteractor,
        private val courseInteractor: CourseInteractor,
        private val resourceInteractor: ResourceInteractor,
        private val authInteractor: AuthInteractor,
        private val view: ResourcesContract.View
) : ResourcesContract.Presenter {
    private lateinit var courseNumbers: List<Int>
    private var isSyncing = false
    private var isStopped = false
    override fun sync() {
        if (!isSyncing)
            authInteractor.execute().flatMap { cookie ->
                Single.zip(courseNumbers.map {
                    resourceInteractor.sync(cookie, courseInteractor.getCourse(it))
                }) {
                    // TODO: This empty funtion is bad, fix it
                }
            }.doOnSubscribe {
                if (!isStopped) view.showLoading()
                isSyncing = true
            }.doFinally {
                if (!isStopped) view.hideLoading()
                isSyncing = false
            }.subscribe({
                view.showSuccess("Resources Successfully updated")
            }, {
                view.showFailed(it.generateMessage())
            })
    }

    override fun onStart() {
        if (isSyncing) view.showLoading()
        isStopped = false
    }

    override fun onStop() {
        isStopped = true
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
        courseNumbers = courses.map { it.second }
        view.updateCourses(courses.toList())
    }

    override fun getCourses(term: String): List<String> = mutableListOf()

}