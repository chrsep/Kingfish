package com.directdev.portal.features.resources

import com.directdev.portal.BasePresenter
import com.directdev.portal.BaseView

/**
 * Created by chris on 04/10/17.
 */
interface ResourcesContract {
    interface View : BaseView<Presenter>

    interface Presenter: BasePresenter {
        fun getSemesters(): List<String>
        fun updateSelectedSemester(selectedTerm: String)
        fun getCourses(term: String): List<String>

    }
}