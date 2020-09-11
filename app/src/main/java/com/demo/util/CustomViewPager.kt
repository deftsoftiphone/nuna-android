package com.demo.util

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import android.view.View.MeasureSpec.makeMeasureSpec
import androidx.viewpager.widget.ViewPager


class CustomViewPager : ViewPager {
    var canSwipe = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        var heightMeasureSpec = heightMeasureSpec
        val child: View? = getChildAt(currentItem)
        if (child != null) {
            child.measure(
                widthMeasureSpec,
                makeMeasureSpec(
                    0,
                    MeasureSpec.UNSPECIFIED
                )
            )
            val h: Int = child.getMeasuredHeight()
            heightMeasureSpec = makeMeasureSpec(
                h,
                EXACTLY
            )
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun canSwipe(canSwipe: Boolean) {
        this.canSwipe = canSwipe
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (canSwipe) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return if (canSwipe) {
            super.onInterceptTouchEvent(event)
        } else false
    }
}