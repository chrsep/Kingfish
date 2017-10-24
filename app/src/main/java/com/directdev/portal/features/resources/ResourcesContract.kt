package com.directdev.portal.features.resources

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView
import com.directdev.portal.models.ResModel
import io.reactivex.Single
import java.io.Serializable

/**
 * Created by chris on 04/10/17.
 */
interface ResourcesContract {
    interface View : BaseView<Presenter> {
        fun updateCourses(courses: List<Pair<String, Int>>)
    }

    interface Presenter : BasePresenter, Serializable {
        fun getSemesters(): List<Pair<Int, String>>
        fun updateSelectedSemester(selectedTerm: Int)
        fun getCourses(term: String): List<String>
        fun getResources(classNumber: Int): ResModel?
        fun sync(courseNumber: List<Int>): Single<Unit>
    }
}