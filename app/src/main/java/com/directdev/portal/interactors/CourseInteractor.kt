package com.directdev.portal.interactors

import com.directdev.portal.network.NetworkHelper
import com.directdev.portal.repositories.CourseRepository
import com.directdev.portal.repositories.TermRepository
import io.reactivex.Single
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 9/3/17.
 *------------------------------------------------------------------------------------------------*/
class CourseInteractor @Inject constructor(
        private val bimayApi: NetworkHelper,
        private val termRepo: TermRepository,
        private val courseRepo: CourseRepository
) {
    fun sync(cookie: String): Single<String> {
        val emptyTerms = termRepo.getTerms().filter {
            val courses = courseRepo.getCourses(it)
            courses.isEmpty()
        }
        return bimayApi.getCourses(cookie, emptyTerms).map {
            courseRepo.saveCourses(it)
            cookie
        }
    }
}