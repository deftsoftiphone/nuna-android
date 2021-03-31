package com.demo.viewPost.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class MyRecycler : RecyclerView {
    var isVerticalScrollingEnabled = true
        private set

    fun enableVerticalScroll(enabled: Boolean) {
        isVerticalScrollingEnabled = enabled
    }

    override fun computeVerticalScrollRange(): Int {
        return if (isVerticalScrollingEnabled) super.computeVerticalScrollRange() else 0
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return if (isVerticalScrollingEnabled) super.onInterceptTouchEvent(e) else false
    }

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context!!, attrs, defStyle) {
    }
}