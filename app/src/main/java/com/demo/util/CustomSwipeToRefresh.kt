package com.demo.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.abs

class CustomSwipeToRefresh(context: Context, attrs: AttributeSet) :
    SwipeRefreshLayout(context, attrs) {
    private var mTouchSlop: Int = 0
    private var mPrevX: Float = 0F

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop;
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                mPrevX = MotionEvent.obtain(ev).x
            }
            MotionEvent.ACTION_MOVE -> {
                val eventX: Float = ev.x
                val xDiff = abs(eventX - mPrevX);

                if (xDiff > mTouchSlop) {
                    return false;
                }
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

}