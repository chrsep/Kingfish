package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/25/17.
 *------------------------------------------------------------------------------------------------*/
class FlagRepository @Inject constructor(val ctx: Context) {
    data class Flags(val isLoggedIn: Boolean, val isStaff: Boolean)

    fun getAll() = Flags(getSignedin(), getStaff())
    fun getSignedin() = ctx.readPref(R.string.isLoggedIn, false)
    fun getStaff() = ctx.readPref(R.string.isLoggedIn, false)
    fun save(isStaff: Boolean = false, isLoggedIn: Boolean = false) {
        ctx.savePref(R.string.isStaff, isStaff)
                .savePref(R.string.isLoggedIn, isLoggedIn)
    }
}