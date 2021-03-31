package com.demo.viewPost.utils

import android.text.Editable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
object ConstantObjects {

    const val VIDEO_LIST = "VIDEO_LIST"
    const val VIDEO_LIST_SAVED = "VIDEO_LIST_SAVED"
    const val Keyboard_Height = "Keyboard_Height"

    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }

    fun getDurationString(durationMs: Long, negativePrefix: Boolean): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)

        return if (hours > 0) {
            String.format(
                Locale.getDefault(), "%s%02d:%02d:%02d",
                if (negativePrefix) "-" else "",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
        } else String.format(
            Locale.getDefault(), "%s%02d:%02d",
            if (negativePrefix) "-" else "",
            minutes,
            seconds - TimeUnit.MINUTES.toSeconds(minutes)
        )
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    // slide the view from below itself to the current position
    fun slideUp(child: View, parent: ViewGroup) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 500
        transition.addTarget(child)

        TransitionManager.beginDelayedTransition(parent, transition)
        child.visibility = View.VISIBLE
    }

    // slide the view from its current position to below itself
    fun slideDown(child: View, parent: ViewGroup) {
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 500
        transition.addTarget(child)

        TransitionManager.beginDelayedTransition(parent, transition)
        child.visibility = View.GONE
    }
}