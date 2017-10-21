package com.directdev.portal.features.resources

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**
 * Created by chris on 04/10/17.
 */
interface ResourcesContract {
    interface View : BaseView<Presenter> {
        fun updateCourses(courses: List<Pair<String, String>>)
    }

    interface Presenter: BasePresenter {
        fun getSemesters(): List<Pair<Int, String>>
        fun updateSelectedSemester(selectedTerm: Int)
        fun getCourses(term: String): List<String>

    }
}