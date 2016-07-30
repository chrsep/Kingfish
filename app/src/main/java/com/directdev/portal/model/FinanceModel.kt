package com.directdev.portal.model

import com.squareup.moshi.Json
import io.realm.RealmObject

open class FinanceModel : RealmObject() {
    @Json(name = "applied_amt")
    open var paymentAmount: String = "N/A"   //".0000"

    @Json(name = "descr")
    open var description: String = "N/A"    //"Variable Tuition Fee"

    @Json(name = "due_dt")
    open var dueDate: String = "N/A"        //"2016-09-28 00:00:00.000"

    @Json(name = "item_amt")
    open var chargeAmount: String = "N/A"   //"5405000.0000"

    @Json(name = "item_effective_dt")
    open var postedDate: String = "N/A"     //"2016-04-21 00:00:00.000"

    @Json(name = "item_term")
    open var term: String = "N/A"           //"2016, Odd Semester"

    @Json(name = "item_type")
    open var type: String = "N/A"           //"100020013050"

    @Json(name = "item_type_cd")
    open var typeId: String = "N/A"         //"C"
}
