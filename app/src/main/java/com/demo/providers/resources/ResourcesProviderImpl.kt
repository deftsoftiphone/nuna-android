package com.demo.providers.resources

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.core.content.res.ResourcesCompat

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getStringArray(id: Int): Array<String> {
        return context.resources.getStringArray(id)
    }

    override fun getColor(id: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context.getColor(id)
        else context.resources.getColor(id)
    }

    override fun getFont(path: String): Typeface {
        return Typeface.createFromAsset(context.resources.assets, path)
    }

    override fun getInt(id: Int): Int {
        return context.resources.getInteger(id)
    }
}