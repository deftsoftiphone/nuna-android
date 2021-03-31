package com.banuba.videoeditor.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.core.animation.doOnEnd
import com.banuba.sdk.core.ui.ext.dimenPx
import com.banuba.videoeditor.R

class RecordButtonView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttrs) {

    companion object {

        private const val DEFAULT_DURATION_MS = 300L
        private const val FINISH_RECORD_DELAY = 200L
    }

    private val outerPart = RecordButtonOuterView(context)

    private val innerOvalPart = View(context).apply {
        setBackgroundResource(R.drawable.ic_record_button_inner_oval)
        elevation = context.dimenPx(R.dimen.record_button_elevation).toFloat()
        val innerOvalSize = context.dimenPx(R.dimen.record_button_inner_part_size)
        layoutParams = LayoutParams(innerOvalSize, innerOvalSize).apply {
            gravity = Gravity.CENTER
        }
    }
    private val innerSquarePart = View(context).apply {
        setBackgroundResource(R.drawable.ic_record_button_inner_square)
        elevation = context.dimenPx(R.dimen.record_button_elevation).toFloat()
        val innerSquareSize = context.dimenPx(R.dimen.record_button_progress_size)
        layoutParams = LayoutParams(innerSquareSize, innerSquareSize).apply {
            gravity = Gravity.CENTER
        }
    }

    private val photoShootAnimation = ObjectAnimator.ofPropertyValuesHolder(
        innerOvalPart,
        PropertyValuesHolder.ofFloat("scaleX", 1F, 0.875F),
        PropertyValuesHolder.ofFloat("scaleY", 1F, 0.875F)
    ).apply {
        duration = DEFAULT_DURATION_MS
        repeatCount = 1
        repeatMode = ValueAnimator.REVERSE
    }

    private val videoShootInnerAnimation = AnimatorSet().apply {
        val ovalAnim = ObjectAnimator.ofPropertyValuesHolder(
            innerOvalPart,
            PropertyValuesHolder.ofFloat("scaleX", 1F, 0F),
            PropertyValuesHolder.ofFloat("scaleY", 1F, 0F)
        ).apply {
            duration = DEFAULT_DURATION_MS
            repeatCount = 0
        }
        val squareAnim = ObjectAnimator.ofPropertyValuesHolder(
            innerSquarePart,
            PropertyValuesHolder.ofFloat("scaleX", 0F, 1F),
            PropertyValuesHolder.ofFloat("scaleY", 0F, 1F)
        ).apply {
            duration = DEFAULT_DURATION_MS
            repeatCount = 0
        }
        playTogether(ovalAnim, squareAnim)
    }

    private val videoShootOuterAnimation = AnimatorSet().apply {
        val circleAnim = ObjectAnimator.ofPropertyValuesHolder(
            outerPart,
            PropertyValuesHolder.ofFloat("scaleX", 1F, 1.5F),
            PropertyValuesHolder.ofFloat("scaleY", 1F, 1.5F)
        ).apply {
            duration = DEFAULT_DURATION_MS
            repeatCount = 0
        }
        playTogether(circleAnim)
    }

    init {
        val outerPartSize = context.dimenPx(R.dimen.record_button_size)
        addView(outerPart, LayoutParams(outerPartSize, outerPartSize).apply {
            gravity = Gravity.CENTER
        })
        addView(innerSquarePart)
        addView(innerOvalPart)
    }

    fun reset() {
        photoShootAnimation.cancel()
        videoShootInnerAnimation.cancel()
    }

    fun animateTakePhoto(onEndCallback: () -> Unit) {
        with(photoShootAnimation) {
            doOnEnd { onEndCallback() }
            start()
        }
    }

    fun animateStartVideoRecord(
        availableDuration: Long,
        onEndCallback: () -> Unit
    ) {
        outerPart.startAnimation(availableDuration)
        with(videoShootInnerAnimation) {
            interpolator = LinearInterpolator()
            removeAllListeners()
            doOnEnd { onEndCallback() }
            start()
        }
        with(videoShootOuterAnimation) {
            interpolator = LinearInterpolator()
            start()
        }
    }

    fun animateStopVideoRecord(onEndCallback: () -> Unit) {
        outerPart.stopAnimation()
        with(videoShootInnerAnimation) {
            interpolator = ReverseInterpolator
            removeAllListeners()
            doOnEnd {
                postDelayed({
                    onEndCallback()
                }, FINISH_RECORD_DELAY)
            }
            start()
        }
        with(videoShootOuterAnimation) {
            interpolator = ReverseInterpolator
            start()
        }
    }

    fun pauseAnimation() {
        outerPart.pauseAnimation()
    }

    fun resumeAnimation() {
        outerPart.resumeAnimation()
    }
}
