package com.banuba.videoeditor.effects

import android.net.Uri
import com.banuba.sdk.core.effects.IEffectDrawable
import com.banuba.sdk.effects.ve.visual.acid.AcidDrawable
import com.banuba.sdk.effects.ve.visual.cathode.CathodeDrawable
import com.banuba.sdk.effects.ve.visual.flash.FlashDrawable
import com.banuba.sdk.effects.ve.visual.glitch.GlithDrawable
import com.banuba.sdk.effects.ve.visual.polaroid.PolaroidDrawable
import com.banuba.sdk.effects.ve.visual.rave.RaveDrawable
import com.banuba.sdk.effects.ve.visual.soul.SoulDrawable
import com.banuba.sdk.effects.ve.visual.tvfoam.TVFoamDrawable
import com.banuba.sdk.effects.ve.visual.vhs.VHSDrawable
import com.banuba.sdk.effects.ve.visual.zoom.ZoomDrawable
import com.banuba.sdk.ve.effects.EditorEffectProvider
import com.banuba.videoeditor.R

sealed class VisualEffects : EditorEffectProvider<IEffectDrawable> {

    object VHS : VisualEffects() {

        override val nameRes: Int
            get() = R.string.visual_effect_vhs

        override val iconRes: Int
            get() = R.drawable.effect_vhs

        override val colorRes: Int
            get() = R.color.darkPink

        override val previewUri: Uri = Uri.EMPTY

        override fun provide() = VHSDrawable()
    }

    object Rave : VisualEffects() {

        override val nameRes: Int
            get() = R.string.visual_effect_rave

        override val iconRes: Int
            get() = R.drawable.effect_rave

        override val colorRes: Int
            get() = R.color.text_on_video_blue

        override val previewUri: Uri = Uri.EMPTY

        override fun provide() = RaveDrawable()
    }

    object Cathode : VisualEffects() {
        override val colorRes: Int
            get() = R.color.text_on_video_brown
        override val iconRes: Int
            get() = R.drawable.effect_cathode
        override val nameRes: Int
            get() = R.string.visual_effect_cathode

        override val previewUri: Uri = Uri.EMPTY

        override fun provide(): IEffectDrawable = CathodeDrawable()
    }

    object Flash : VisualEffects() {

        override val nameRes: Int
            get() = R.string.visual_effect_flash

        override val iconRes: Int
            get() = R.drawable.effect_flash

        override val colorRes: Int
            get() = R.color.text_on_video_purple

        override val previewUri: Uri = Uri.EMPTY

        override fun provide() = FlashDrawable()
    }

    object Soul : VisualEffects() {

        override val nameRes: Int
            get() = R.string.visual_effect_soul

        override val iconRes: Int
            get() = R.drawable.effect_soul

        override val colorRes: Int
            get() = R.color.text_on_video_yellow

        override val previewUri: Uri = Uri.EMPTY

        override fun provide() = SoulDrawable()
    }

    object Zoom : VisualEffects() {
        override val colorRes: Int
            get() = R.color.text_on_video_darkBlue
        override val iconRes: Int
            get() = R.drawable.effect_zoom
        override val nameRes: Int
            get() = R.string.visual_effect_zoom

        override val previewUri: Uri = Uri.EMPTY

        override fun provide(): IEffectDrawable = ZoomDrawable()
    }

    object TvFoam : VisualEffects() {

        override val nameRes: Int
            get() = R.string.visual_effect_tv_foam

        override val iconRes: Int
            get() = R.drawable.effect_tv_foam

        override val colorRes: Int
            get() = R.color.red

        override val previewUri: Uri = Uri.EMPTY

        override fun provide() = TVFoamDrawable()
    }

    object Polaroid : VisualEffects() {
        override val colorRes: Int
            get() = R.color.text_on_video_mintGreen
        override val iconRes: Int
            get() = R.drawable.effect_polaroid
        override val nameRes: Int
            get() = R.string.visual_effect_polaroid

        override val previewUri: Uri = Uri.EMPTY

        override fun provide(): IEffectDrawable = PolaroidDrawable()
    }

    object Glitch : VisualEffects() {
        override val colorRes: Int
            get() = R.color.text_on_video_gray
        override val iconRes: Int
            get() = R.drawable.effect_glitch
        override val nameRes: Int
            get() = R.string.visual_effect_glitch

        override val previewUri: Uri = Uri.EMPTY

        override fun provide(): IEffectDrawable = GlithDrawable()
    }

    object Acid : VisualEffects() {
        override val colorRes: Int
            get() = R.color.text_on_video_white
        override val iconRes: Int
            get() = R.drawable.effect_acid
        override val nameRes: Int
            get() = R.string.visual_effect_acid

        override val previewUri: Uri = Uri.EMPTY

        override fun provide(): IEffectDrawable = AcidDrawable()
    }
}