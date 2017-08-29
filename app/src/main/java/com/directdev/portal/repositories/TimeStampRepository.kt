package com.directdev.portal.repositories

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
class TimeStampRepository @Inject constructor(
        private val dateProvider: DateTime,
        private val formatter: DateTimeFormatter
) {
    fun today(): Date = dateProvider.withTimeAtStartOfDay().toDate()
    fun todayString(): String = dateProvider.withTimeAtStartOfDay().toString(formatter)
}