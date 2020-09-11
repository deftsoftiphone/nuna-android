package com.demo.providers.resources

import android.graphics.Typeface

interface ResourcesProvider {
    fun getString(id: Int): String
    fun getStringArray(id: Int): Array<String>
    fun getColor(id: Int): Int
    fun getInt(id: Int): Int
    fun getFont(path:String):Typeface
}