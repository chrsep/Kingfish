package com.directdev.portal.models

import com.squareup.moshi.Json

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/29/17.
 *------------------------------------------------------------------------------------------------*/
class TermIntermidiaryModel(
        @Json(name = "Period")
        val period: List<TermModel>
)