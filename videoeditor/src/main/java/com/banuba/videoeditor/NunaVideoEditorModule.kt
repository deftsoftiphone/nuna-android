package com.banuba.videoeditor

import android.app.Application
import com.banuba.android.sdk.camera.BanubaCameraSdkManager
import com.banuba.android.sdk.camera.CameraSdkManager
import com.banuba.sdk.cameraui.data.CameraRecordingAnimationProvider
import com.banuba.sdk.cameraui.data.CameraTimerActionProvider
import com.banuba.sdk.cameraui.data.CameraTimerStateProvider
import com.banuba.sdk.core.AREffectPlayerProvider
import com.banuba.sdk.core.IUtilityManager
import com.banuba.sdk.effectplayer.adapter.BanubaAREffectPlayerProvider
import com.banuba.sdk.effectplayer.adapter.BanubaClassFactory
import com.banuba.sdk.ve.effects.EditorEffects
import com.banuba.sdk.ve.flow.ExportFlowManager
import com.banuba.sdk.ve.flow.ExportResultHandler
import com.banuba.sdk.ve.flow.FlowEditorModule
import com.banuba.videoeditor.effects.TimeEffects
import com.banuba.videoeditor.effects.VisualEffects
import com.banuba.videoeditor.widget.CustomOnCameraTimerActionProvider
import com.banuba.videoeditor.widget.NewCameraTimerStateProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.named

class NunaVideoEditorModule : FlowEditorModule() {

    override val cameraTimerActionProvider: BeanDefinition<CameraTimerActionProvider> =
        factory(override = true) {
            CustomOnCameraTimerActionProvider()
        }

    override val cameraTimerStateProvider: BeanDefinition<CameraTimerStateProvider> =
        factory {
            NewCameraTimerStateProvider()
        }

    override val effectPlayerManager: BeanDefinition<AREffectPlayerProvider> =
        single(override = true) {
            BanubaAREffectPlayerProvider(
                mediaSizeProvider = get(),
                token = androidContext().getString(R.string.video_editor_token)
            )
        }

    override val banubaSdkManager: BeanDefinition<CameraSdkManager> = single(override = true) {
        val context = get<Application>().applicationContext

        val utilityManager: IUtilityManager = BanubaClassFactory.createUtilityManager(
            context = get(),
            context.getString(R.string.effect_player_token)
        )

        val effectPlayerProvider: AREffectPlayerProvider = get()

        BanubaCameraSdkManager.createInstance(
            context,
            effectPlayerProvider,
            utilityManager
        )
    }

    override val exportFlowManager: BeanDefinition<ExportFlowManager> = single {
        NunaExportFlowManager(
            exportDataProvider = get(),
            editorSessionHelper = get(),
            exportDir = get(named("exportDir"))
        )
    }

    override val exportResultHandler: BeanDefinition<ExportResultHandler> = single {
        NunaExportResultHandler()
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