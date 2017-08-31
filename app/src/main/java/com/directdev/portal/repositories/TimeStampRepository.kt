package com.directdev.portal.repositories

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
abstract class TimeStampRepository(
        private val formatter: DateTimeFormatter
) {
    fun today(): DateTime = DateTime.now().withTimeAtStartOfDay()
    fun todayString(): String = DateTime.now().withTimeAtStartOfDay().toString(formatter)
    abstract fun updateLastSyncDate()
    abstract fun isSyncOverdue(): Boolean
}
