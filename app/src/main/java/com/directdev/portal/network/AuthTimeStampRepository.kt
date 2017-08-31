package com.directdev.portal.network

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.repositories.TimeStampRepository
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