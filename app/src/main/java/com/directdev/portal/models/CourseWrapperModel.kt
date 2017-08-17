package com.directdev.portal.models

import com.squareup.moshi.Json

/**
 * Created by chris on 9/12/2016.
 */

open class CourseWrapperModel(
        @Json(name = "Courses")
        open var courses: List<CourseModel> = mutableListOf()
)
