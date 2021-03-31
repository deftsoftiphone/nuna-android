package com.banuba.videoeditor.widget

import android.annotation.SuppressLint
import com.banuba.sdk.cameraui.data.*
import com.banuba.sdk.core.ui.ext.showTopToast
import com.banuba.sdk.core.ui.widget.TitledImageView
import com.banuba.videoeditor.R

class CustomOnCameraTimerActionProvider : CameraTimerActionProvider {

     val timerStates = listOf(
        TimerEntry(
            durationMs = 0,
            iconResId = R.drawable.ic_stopwatch1_off
        ),
        TimerEntry(
            durationMs = 3000,
            iconResId = R.drawable.ic_stopwatch1_on
        )
    )

    @SuppressLint("StringFormatInvalid")
    override fun handleAction(
        handler: OnTimerButtonActionHandler,
        currentState: TimerEntry?,
        timerButton: TitledImageView
    ) {
        val nextIndex = (timerStates.indexOf(currentState) + 1) % timerStates.size
        val nextState = timerStates[nextIndex]
        val context = timerButton.context
        val toastMsg = if (nextState.isEnabled) {
            context.getString(
                R.string.notification_timer_on,
                nextState.durationMs / 1000
            )
        } else {
            context.getString(R.string.notification_timer_off)
        }
        context.showTopToast(toastMsg)
        handler.handleNewState(nextState)
    }

     fun getInitialState(timerButton: TitledImageView) = timerStates.first()
}