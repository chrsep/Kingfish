package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/27/17.
 *------------------------------------------------------------------------------------------------*/
abstract class TimeStampRepository(
        private val formatter: DateTimeFormatter,
        private val ctx: Context
) {
    abstract fun getId(): Int
    fun today(): DateTime = DateTime.now()
    fun todayString(): String = DateTime.now().toString(formatter)
    fun updateLastSyncDate() {
        ctx.savePref(getId(), DateTime.now().toString())
    }

    fun getLastSync(): DateTime {
        val lastSync = ctx.readPref(getId(), "")
        return if (lastSync != "") DateTime.parse(lastSync) else DateTime().minusYears(1)
    }
}
