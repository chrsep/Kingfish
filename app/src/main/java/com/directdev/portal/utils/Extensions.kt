package com.directdev.portal.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.crashlytics.android.Crashlytics
import com.directdev.portal.BuildConfig
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.NumberFormat
import java.util.*
import javax.net.ssl.SSLException

/*--------------------------------------------------------------------------------------------------
 *
 * These are extension functions for common repetitive things (Ex. Saving preferences, Showing
 * SnackBars, etc...), created as an effort to make the code more readable and concise
 *
 *------------------------------------------------------------------------------------------------*/
/*--------------------------------------------------------------------------------------------------
 * Interacting with Shared Preferences
 *------------------------------------------------------------------------------------------------*/
/**-------------------------------------------------------------------------------------------------
 * Extension Function for saving shared preference
 *------------------------------------------------------------------------------------------------*/

@SuppressLint("CommitPrefEdits")
fun Context.savePref(@StringRes id: Int, data: Any): Context {
    val key = getString(id)
    val editor = getSharedPreferences("com.kingfish", Context.MODE_PRIVATE).edit()
    when (data) {
        is String -> editor.putString(key, data)
        is Boolean -> editor.putBoolean(key, data)
        is Float -> editor.putFloat(key, data)
        is Int -> editor.putInt(key, data)
        is Long -> editor.putLong(key, data)
        else -> return this
    }
    editor.commit()
    return this
}

/**-------------------------------------------------------------------------------------------------
 * Extension Function for reading shared preference
 *------------------------------------------------------------------------------------------------*/

fun Context.readPref(@StringRes id: Int, defaultValue: String = "", preferenceId: String = "com.kingfish"): String {
    val sp = getSharedPreferences(preferenceId, Context.MODE_PRIVATE)
    val key = getString(id)
    return sp.getString(key, defaultValue)
}

/**-------------------------------------------------------------------------------------------------
 * Extension Function for reading shared preference
 *------------------------------------------------------------------------------------------------*/

fun Context.readPref(@StringRes id: Int, defaultValue: Boolean, preferenceId: String = "com.kingfish"): Boolean {
    val sp = getSharedPreferences(preferenceId, Context.MODE_PRIVATE)
    val key = getString(id)
    return sp.getBoolean(key, defaultValue)
}

/**-------------------------------------------------------------------------------------------------
 * Extension function for clearing shared preference
 *------------------------------------------------------------------------------------------------*/

@SuppressLint("CommitPrefEdits")
fun Context.clearPref() {
    val editor = getSharedPreferences("com.kingfish", Context.MODE_PRIVATE).edit()
    editor.clear()
    editor.commit()
}

/*--------------------------------------------------------------------------------------------------
 * Interacting with SnackBars
 *------------------------------------------------------------------------------------------------*/
/**-------------------------------------------------------------------------------------------------
 * Extension function for creating and show snack bar
 *------------------------------------------------------------------------------------------------*/

fun View.snack(msg: Any, length: Int = Snackbar.LENGTH_SHORT, option: Snackbar.() -> Unit = {}) {
    val snack = Snackbar.make(this, msg.toString(), length)
    snack.option()
    snack.show()
}

/**-------------------------------------------------------------------------------------------------
 * Extension Function for adding an action to a SnackBar (the button on a SnackBar)
 *------------------------------------------------------------------------------------------------*/

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}

/*--------------------------------------------------------------------------------------------------
 * Interacting with Views
 *------------------------------------------------------------------------------------------------*/
/**-------------------------------------------------------------------------------------------------
 * Extension Function for adding a callback that response to a 'enter key' press on a view
 *------------------------------------------------------------------------------------------------*/

fun View.onEnter(callback: () -> Unit) {
    setOnKeyListener { _, _, keyEvent ->
        if (keyEvent?.action == KeyEvent.ACTION_DOWN && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
            callback()
        false
    }
}

/*--------------------------------------------------------------------------------------------------
 * Other helper function
 *------------------------------------------------------------------------------------------------*/
/**-------------------------------------------------------------------------------------------------
 * Turn a string of number into rupiah currency Eg. 12345 -> Rp. 123.45
 *------------------------------------------------------------------------------------------------*/

fun String.formatToRupiah() = "Rp. ${NumberFormat.getNumberInstance(Locale.US).format(toFloat())}"

/**-------------------------------------------------------------------------------------------------
 * Format a date into string with a given pattern
 *------------------------------------------------------------------------------------------------*/

fun DateTime.toString(pattern: String): String = toString(DateTimeFormat.forPattern(pattern))

/**-------------------------------------------------------------------------------------------------
 * Check if network is available
 *------------------------------------------------------------------------------------------------*/

fun ConnectivityManager.isNetworkAvailable(): Boolean =
        activeNetworkInfo != null && activeNetworkInfo.isConnected


fun <T> Single<T>.observeOnMainThread(): Single<T> = observeOn(AndroidSchedulers.mainThread())


fun <T> Single<T>.subscribeOnIo(): Single<T> = subscribeOn(Schedulers.io())


fun <T> Single<T>.defaultThreads(): Single<T> = subscribeOnIo().observeOnMainThread()

fun <T> List<List<T>>.flatten(): MutableList<T> {
    val newList = mutableListOf<T>()
    forEach { newList.addAll(it) }
    return newList
}

fun Throwable.generateMessage(): String {
    val message = when (this) {
        is SocketTimeoutException -> "Request Timed Out"
        is HttpException -> "Binusmaya's server seems to be offline, try again later"
        is ConnectException -> "Failed to connect to Binusmaya"
        is SSLException -> "Failed to connect to Binusmaya"
        is UnknownHostException -> "Failed to connect to Binusmaya"
        is NoSuchMethodException -> "Captcha cancelled"
        is IndexOutOfBoundsException -> "Binusmaya server is acting weird, try again later"
        is SigninException -> getMessage(message ?: "")
        is IOException -> "Binusmaya is giving us weird data, contact us on GitHub (Setting -> Issue Tracker)"
        else -> {
            Crashlytics.setBool("unknownCrash", true)
            "We have no idea what went wrong, but we have received the error log, we'll look into this"
        }
    }
    if (BuildConfig.DEBUG) Log.e("Network Error", this.toString())
    Crashlytics.logException(this)
    return message
}

fun getMessage(message: String): String = when (message) {
    "../login/?error=5" -> "User role haven't been mapped"
    "../login/?error=4" -> "Invalid captcha"
    "../login/?error=3" -> "Username/Password must be filled"
    "../login/?error=2" -> "User not found"
    "../login/?error=1" -> "Invalid username/password"
    else -> "We have no idea what went wrong, but we have received the error log, we'll look into this"
}

fun String.getInitials() = split(" ").fold("") { acc, s -> acc + s[0] }