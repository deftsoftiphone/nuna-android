package com.banuba.videoeditor.widget

import com.banuba.sdk.cameraui.data.CameraTimerStateProvider
import com.banuba.sdk.cameraui.data.TimerEntry
import com.banuba.videoeditor.R

class NewCameraTimerStateProvider : CameraTimerStateProvider {
    override val timerStates = listOf(
        TimerEntry(
            durationMs = 0,
            iconResId = R.drawable.ic_stopwatch1_off
        ),
        TimerEntry(
            durationMs = 3000,
            iconResId = R.drawable.ic_stopwatch1_on
        )
    )
}