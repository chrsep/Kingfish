package com.directdev.portal.repositories

import android.content.Context
import com.crashlytics.android.Crashlytics
import com.directdev.portal.R
import com.directdev.portal.utils.savePref
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**-------------------------------------------------------------------------------------------------
 * Created by chris on 8/26/17.
 *------------------------------------------------------------------------------------------------*/
class ProfileRepository @Inject constructor(val ctx: Context) {
    fun save(profile: JSONObject) {
        try {
            ctx.savePref(R.string.major, profile.getString("ACAD_PROG_DESCR"))
            ctx.savePref(R.string.degree, profile.getString("ACAD_CAREER_DESCR"))
            ctx.savePref(R.string.birthday, profile.getString("BIRTHDATE"))
            ctx.savePref(R.string.name, profile.getString("NAMA"))
            ctx.savePref(R.string.nim, profile.getString("NIM"))
        } catch (err: JSONException) {
            Crashlytics.log(profile.toString())
            Crashlytics.logException(err)
            throw err
        }
    }
}