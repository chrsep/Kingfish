package com.directdev.portal.utils

import android.content.Context
import android.support.annotation.StringRes

object Prefs{
    val PREF_ID = "com.kingfish"
    fun save(ctx: Context, key: String , value: Any) {
        val sp = ctx.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
        val editor = sp.edit()
        when (value){
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            else -> return
        }
        editor.apply()
    }

    fun read(ctx: Context, @StringRes id: Int, defaultValue: Any): Any {
        val sp = ctx.getSharedPreferences(PREF_ID, Context.MODE_PRIVATE)
        val key = ctx.getString(id)
        when(defaultValue){
            is String -> return sp.getString(key, defaultValue)
            is Boolean -> return sp.getBoolean(key, defaultValue)
            is Float -> return sp.getFloat(key, defaultValue)
            is Int -> return sp.getInt(key, defaultValue)
            is Long -> return sp.getLong(key, defaultValue)
            else -> return 0
        }
    }
}
