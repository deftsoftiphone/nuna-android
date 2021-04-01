package com.banuba.videoeditor

import com.banuba.sdk.cameraui.data.CameraRecordingAnimationProvider
import com.banuba.sdk.cameraui.data.CameraTimerActionProvider
import com.banuba.sdk.cameraui.data.CameraTimerStateProvider
import com.banuba.sdk.ve.effects.EditorEffects
import com.banuba.sdk.ve.flow.ExportFlowManager
import com.banuba.sdk.ve.flow.FlowEditorModule
import com.banuba.sdk.ve.flow.export.ForegroundExportFlowManager
import com.banuba.videoeditor.effects.TimeEffects
import com.banuba.videoeditor.effects.VisualEffects
import com.banuba.videoeditor.widget.CustomOnCameraTimerActionProvider
import com.banuba.videoeditor.widget.NewCameraTimerStateProvider
import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.named

class NunaVideoEditorModule : FlowEditorModule() {

    override val cameraTimerActionProvider: BeanDefinition<CameraTimerActionProvider> =
        factory(override = true) {
            CustomOnCameraTimerActionProvider()
        }

    override val cameraTimerStateProvider: BeanDefinition<CameraTimerStateProvider> =
        factory(override = true) {
            NewCameraTimerStateProvider()
        }

    override val exportFlowManager: BeanDefinition<ExportFlowManager> = single(override = true) {
        ForegroundExportFlowManager(
            exportDataProvider = get(),
            editorSessionHelper = get(),
            exportDir = get(named("exportDir")),
            mediaFileNameHelper = get(),
            shouldClearSessionOnFinish = true
        )
    }

    override val editorEffects: BeanDefinition<EditorEffects> = single(override = true) {

        val visualEffects = listOf(
            VisualEffects.VHS,
            VisualEffects.Rave,
            VisualEffects.Cathode,
            VisualEffects.Flash,
            VisualEffects.Soul,
            VisualEffects.Zoom,
            VisualEffects.TvFoam,
            VisualEffects.Polaroid,
            VisualEffects.Glitch,
            VisualEffects.Acid
        )
        val timeEffects = listOf(
            TimeEffects.SlowMo(),
            TimeEffects.Rapid()
        )

        EditorEffects(
            visual = visualEffects,
            time = timeEffects,
            equalizer = emptyList()
        )
    }

    override val cameraRecordingAnimationProvider: BeanDefinition<CameraRecordingAnimationProvider> =
        factory(override = true) {
            NunaCameraRecordingAnimationProvider(get())
        }

}