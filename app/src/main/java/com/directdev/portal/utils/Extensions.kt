package com.directdev.portal.utils

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View

fun Any.savePref(ctx: Context, @StringRes id: Int) {
    val key = ctx.getString(id)
    val editor = ctx.getSharedPreferences("com.kingfish", Context.MODE_PRIVATE).edit()
    when (this) {
        is String -> editor.putString(key, this)
        is Boolean -> editor.putBoolean(key, this)
        is Float -> editor.putFloat(key, this)
        is Int -> editor.putInt(key, this)
        is Long -> editor.putLong(key, this)
        else -> return
    }
    editor.commit()
}

fun Context.readPref(@StringRes id: Int, defaultValue: Any): Any {
    val sp = this.getSharedPreferences("com.kingfish", Context.MODE_PRIVATE)
    val key = this.getString(id)
    return when (defaultValue) {
        is String -> sp.getString(key, defaultValue)
        is Boolean -> sp.getBoolean(key, defaultValue)
        is Float -> sp.getFloat(key, defaultValue)
        is Int -> sp.getInt(key, defaultValue)
        is Long -> sp.getLong(key, defaultValue)
        else -> 0
    }
}

fun View.snack(msg: String, length: Int = Snackbar.LENGTH_SHORT, option: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, msg, length)
    snack.option()
    snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

