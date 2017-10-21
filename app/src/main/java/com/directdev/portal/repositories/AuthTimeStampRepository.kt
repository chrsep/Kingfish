package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.R
import org.joda.time.format.DateTimeFormatter
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/31/17.
 *------------------------------------------------------------------------------------------------*/
class AuthTimeStampRepository @Inject constructor(
        formatter: DateTimeFormatter,
        ctx: Context
) : TimeStampRepository(formatter, ctx) {
    override fun getId() = R.string.auth_last_try
}