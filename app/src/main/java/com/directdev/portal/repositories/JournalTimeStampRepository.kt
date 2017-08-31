package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class JournalTimeStampRepository @Inject constructor(
        formatter: DateTimeFormatter,
        private val ctx: Context
) : TimeStampRepository(formatter) {
    override fun updateLastSyncDate() {
        ctx.savePref(R.string.journal_last_sync, DateTime.now().toString())
    }

    override fun isSyncOverdue(): Boolean {
        val lastSync = ctx.readPref(R.string.journal_last_sync, "")
        if (lastSync != "") {
            val lastUpdate = DateTime.parse(lastSync)
            val minutesInt = Minutes.minutesBetween(lastUpdate, today()).minutes
            return minutesInt > 5
        }
        return true
    }
}