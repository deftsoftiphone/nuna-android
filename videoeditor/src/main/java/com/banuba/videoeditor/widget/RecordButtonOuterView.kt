package com.banuba.videoeditor.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.banuba.sdk.core.ui.ext.dimen
import com.banuba.videoeditor.R

class RecordButtonOuterView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attr, defStyle) {

    private var circleWidth = context.dimen(R.dimen.record_button_circle_idle_width)
        set(value) {
            field = value
            gradientPaint.strokeWidth = value
            whitePaint.strokeWidth = value
            isDrawAreaMeasured = false
        }
    private val circleColors = context.resources.getIntArray(R.array.throbber_gradient_colors)
    private var circleDrawArea = RectF()

    private var isDrawAreaMeasured = false

    private val gradientPaint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = circleWidth
            shader = SweepGradient(
                measuredWidth.toFloat() / 2,
                measuredHeight.toFloat() / 2,
                circleColors,
                null
            )
        }
    }
    private val whitePaint by lazy(LazyThreadSafetyMode.NONE) {
        Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeWidth = circleWidth
        }
    }

    private var gradientSweepAngle = 0F

    private val animator = ValueAnimator().apply {
        repeatCount = 0
        setFloatValues(0F, 360F)
        interpolator = LinearInterpolator()
        addUpdateListener {
            gradientSweepAngle = it.animatedValue as Float
            invalidate()
        }
    }

    init {
        //Because 0 degrees angle correspond to 3 o'clock on a watch
        rotation = -90f
    }

    override fun onDraw(canvas: Canvas) {
        if (!isDrawAreaMeasured) {
            isDrawAreaMeasured = true
            circleDrawArea = RectF(
                0f + circleWidth,
                0f + circleWidth,
                measuredWidth.toFloat() - circleWidth,
                measuredHeight.toFloat() - circleWidth
            )
        }
        with(canvas) {
            save()
            rotate(gradientSweepAngle, measuredWidth.toFloat() / 2, measuredHeight.toFloat() / 2)
            drawArc(circleDrawArea, 0f, 360F, false, gradientPaint)
            restore()
            drawArc(
                circleDrawArea,
                gradientSweepAngle,
                360F - gradientSweepAngle,
                false,
                whitePaint
            )
        }
    }

    fun startAnimation(availableDuration: Long) {
        circleWidth = context.dimen(R.dimen.record_button_circle_progress_width)
        animator.duration = availableDuration
        animator.cancel()
        animator.currentPlayTime = 0
    }

    fun stopAnimation() {
        circleWidth = context.dimen(R.dimen.record_button_circle_idle_width)
        animator.cancel()
        gradientSweepAngle = 0F
        invalidate()
    }

    fun pauseAnimation() {
        animator.pause()
    }

    fun resumeAnimation() {
        animator.start()
    }
}