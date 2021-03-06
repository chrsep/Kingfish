package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.R
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 9/2/17.
 *------------------------------------------------------------------------------------------------*/
class TermTimeStampRepository @Inject constructor(
        formatter: DateTimeFormatter,
        ctx: Context
) : TimeStampRepository(formatter, ctx) {
    override fun getId() = R.string.term_last_try
}