package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

open class ScoreModel(
        @Json(name = "class")
        open var classId: String = "N/A", //"LB02"

        @Json(name = "course")
        open var courseName: String = "N/A", //""Advanced Object Oriented Prog"

        @Json(name = "course_grade")
        open var courseGradeTotal: String = "N/A", //"A"

        @Json(name = "grade")
        open var courseScoreTotal: String = "N/A", //"91"

        @Json(name = "kodemtk")
        open var courseId: String = "N/A", //"COMP6099"

        @Json(name = "lam")
        open var scoreType: String = "N/A", //"ASSIGNMENT"

        open var score: String = "N/A", //"100"

        open var scu: String = "N/A", //"2"

        open var weight: String = "N/A"                  //""20%""
) : RealmObject()
