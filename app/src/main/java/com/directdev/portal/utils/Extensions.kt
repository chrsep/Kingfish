package com.directdev.portal.utils

import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.KeyEvent
import android.view.View
import org.jetbrains.anko.onKey
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.text.NumberFormat
import java.util.*

fun Context.savePref(data: Any, @StringRes id: Int) {
    val key = getString(id)
    val editor = getSharedPreferences("com.kingfish", Context.MODE_PRIVATE).edit()
    when (data) {
        is String -> editor.putString(key, data)
        is Boolean -> editor.putBoolean(key, data)
        is Float -> editor.putFloat(key, data)
        is Int -> editor.putInt(key, data)
        is Long -> editor.putLong(key, data)
        else -> return
    }
    editor.commit()
}

fun Context.readPref(@StringRes id: Int, defaultValue: String, preferenceId: String = "com.kingfish"): String {
    val sp = this.getSharedPreferences(preferenceId, Context.MODE_PRIVATE)
    val key = this.getString(id)
    return sp.getString(key, defaultValue)
}

fun Context.readPref(@StringRes id: Int, defaultValue: Boolean, preferenceId: String = "com.kingfish"): Boolean {
    val sp = this.getSharedPreferences(preferenceId, Context.MODE_PRIVATE)
    val key = this.getString(id)
    return sp.getBoolean(key, defaultValue)
}

fun Context.clearPref() {
    val editor = getSharedPreferences("com.kingfish", Context.MODE_PRIVATE).edit()
    editor.clear()
    editor.commit()
}

fun View.snack(msg: Any, length: Int = Snackbar.LENGTH_SHORT, option: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, msg.toString(), length)
    snack.option()
    snack.show()
}

fun View.onEnter(callback: () -> Unit) {
    onKey { view, i, keyEvent ->
        if (keyEvent?.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
            callback()
        false
    }
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

fun ConnectivityManager.isNetworkAvailable(): Boolean =
        activeNetworkInfo != null && activeNetworkInfo.isConnected

fun String.formatToRupiah() = "Rp. " + NumberFormat
        .getNumberInstance(Locale.US)
        .format(toFloat())

fun DateTime.toString(pattern: String) = toString(DateTimeFormat.forPattern(pattern))