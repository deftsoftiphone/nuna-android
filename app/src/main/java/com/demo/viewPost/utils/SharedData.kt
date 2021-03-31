package com.demo.viewPost.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
object SharedData {
    private const val PREFS_NAME = "NunaMCT"

    // For Boolean
    fun saveBool(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        val editor: SharedPreferences.Editor
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = settings.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBool(context: Context, key: String?): Boolean {
        val text: Boolean
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        text = settings.getBoolean(key, false)
        return text
    }
    // For Int
    fun saveInt(
        context: Context,
        key: String?,
        value: Int
    ) {
        val editor: SharedPreferences.Editor
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        editor = settings.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(context: Context, key: String?): Int {
        val text: Int
        val settings: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        text = settings.getInt(key, 0)
        return text
    }
}