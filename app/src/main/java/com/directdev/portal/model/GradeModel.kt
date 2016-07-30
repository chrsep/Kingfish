package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmList

open class GradeModel() {
    open var credit = CreditModel()

    @Json(name = "grading_list")
    open var gradings: List<GradingModel> = mutableListOf(GradingModel())

    @Json(name = "score")
    open var scores: List<ScoreModel> = RealmList(ScoreModel())

    @Json(name = "strm")
    open var term = "N/A"
}
