package com.directdev.portal.repositories

import android.content.Context
import com.directdev.portal.R
import com.directdev.portal.utils.readPref
import com.directdev.portal.utils.savePref
import javax.inject.Inject

class UserCredRepository @Inject constructor(val ctx: Context) {

    data class Credentials(val username: String, val password: String, val cookie: String)

    fun save(username: String, password: String, cookie: String) {
        ctx.savePref(R.string.username, username)
                .savePref(R.string.password, password)
                .savePref(R.string.cookie, cookie)
    }

    fun getAll() = Credentials(getUsername(), getPassword(), getCookie())
    fun getUsername() = ctx.readPref(R.string.username, "")
    fun getPassword() = ctx.readPref(R.string.password, "")
    fun getCookie() = ctx.readPref(R.string.cookie, "")
}