package com.banuba.videoeditor.effects

import android.net.Uri
import com.banuba.sdk.core.effects.IEffectDrawable
import com.banuba.sdk.effects.ve.visual.mask.MaskDrawable
import com.banuba.sdk.ve.effects.EditorEffectProvider
import com.banuba.sdk.ve.effects.EditorEffects
import com.banuba.videoeditor.R

sealed class MaskEffects : EditorEffectProvider<IEffectDrawable> {

    abstract val path: Uri

    override val previewUri: Uri
        get() = path.buildUpon().appendEncodedPath("preview.png").build()

    data class Angel(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("AngelLow").build()

        override val nameRes: Int = R.string.mask_effect_angel
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.yellow

        override fun provide() = MaskDrawable(path)
    }

    data class Rabbit(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Rabbit").build()

        override val nameRes: Int = R.string.mask_effect_rabbit
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.cyan

        override fun provide() = MaskDrawable(path)
    }

    data class SpringCat(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("SpringCat").build()

        override val nameRes: Int = R.string.mask_effect_spring_cat
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.greenNeon

        override fun provide() = MaskDrawable(path)
    }

    data class Fashion(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Fashion").build()

        override val nameRes: Int = R.string.mask_effect_fashion
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.blue
        override fun provide() = MaskDrawable(path)
    }

    data class AfroCurly(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("AfroCurly").build()

        override val nameRes: Int = R.string.mask_effect_afro_curly
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.darkPink

        override fun provide() = MaskDrawable(path)
    }

    data class AlbertEinstein(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("AlbertEinstein").build()

        override val nameRes: Int = R.string.mask_effect_albert_einstein
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.violet

        override fun provide() = MaskDrawable(path)
    }

    data class AutumnLeafBand(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("AutumnLeafBand").build()

        override val nameRes: Int = R.string.mask_effect_autumn_leaf_band
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.codeGray

        override fun provide() = MaskDrawable(path)
    }

    data class BabyCatHat(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BabyCatHat").build()

        override val nameRes: Int = R.string.mask_effect_baby_cat_hat
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.red

        override fun provide() = MaskDrawable(path)
    }

    data class BakerBoyCap(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BakerBoyCap2").build()

        override val nameRes: Int = R.string.mask_effect_baker_boy_cap
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_white

        override fun provide() = MaskDrawable(path)
    }

    data class Retrowave(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Retrowave").build()

        override val nameRes: Int = R.string.mask_effect_retrowave
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_gray

        override fun provide() = MaskDrawable(path)
    }

    data class BeautyBokeh(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BeautyBokeh").build()

        override val nameRes: Int = R.string.mask_effect_beauty_bokeh
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_mintGreen

        override fun provide() = MaskDrawable(path)
    }

    data class BikerBandanaMale(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BikerBandanaMale").build()

        override val nameRes: Int = R.string.mask_effect_biker_bandana_male
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_darkBlue

        override fun provide() = MaskDrawable(path)
    }

    data class BrilliantFlowersCrown(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BrilliantFlowersCrown").build()

        override val nameRes: Int = R.string.mask_effect_brilliant_flowers_crown
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_yellow

        override fun provide() = MaskDrawable(path)
    }

    data class BurningMan(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("BurningMan").build()

        override val nameRes: Int = R.string.mask_effect_burning_man
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_purple

        override fun provide() = MaskDrawable(path)
    }

    data class Butterfly(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("butterfly").build()

        override val nameRes: Int = R.string.mask_effect_butterfly
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_brown

        override fun provide() = MaskDrawable(path)
    }

    data class Devil(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Devil").build()

        override val nameRes: Int = R.string.mask_effect_devil
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_blue

        override fun provide() = MaskDrawable(path)
    }

    data class Doggy(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Doggy").build()

        override val nameRes: Int = R.string.mask_effect_doggy
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_black

        override fun provide() = MaskDrawable(path)
    }

    data class EyesOnFire(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("EyesOnFire").build()
        override val nameRes: Int = R.string.mask_effect_eyes_on_fire
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_mainGray

        override fun provide() = MaskDrawable(path)
    }

    data class FaceNightClub(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("FaceNightClub").build()
        override val nameRes: Int = R.string.mask_effect_face_night_club
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_red

        override fun provide() = MaskDrawable(path)
    }

    data class Snake(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("Snake").build()
        override val nameRes: Int = R.string.mask_effect_snake
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.yellow

        override fun provide() = MaskDrawable(path)
    }

    data class HeartsBillboard(val effectsUri: Uri) : MaskEffects() {

        override val path: Uri =
            effectsUri.buildUpon().appendPath("HeartsBillboard").build()
        override val nameRes: Int = R.string.mask_effect_hearts_billboard
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_blue

        override fun provide() = MaskDrawable(path)
    }

    data class PeachHairstyle(val effectsUri: Uri) : MaskEffects() {
        override val path: Uri =
            effectsUri.buildUpon().appendPath("PeachHairstyle").build()
        override val nameRes: Int = R.string.mask_effect_peach_hairstyle
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_brown

        override fun provide() = MaskDrawable(path)
    }

    data class PUBG(val effectsUri: Uri) : MaskEffects() {
        override val path: Uri =
            effectsUri.buildUpon().appendPath("PUBG").build()
        override val nameRes: Int = R.string.mask_effect_pubg
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_purple

        override fun provide() = MaskDrawable(path)
    }

    data class SpiderFrozen(val effectsUri: Uri) : MaskEffects() {
        override val path: Uri =
            effectsUri.buildUpon().appendPath("SpiderFrozen").build()
        override val nameRes: Int = R.string.mask_effect_spider_frozen
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.text_on_video_mintGreen

        override fun provide() = MaskDrawable(path)
    }

    data class Vampire(val effectsUri: Uri) : MaskEffects() {
        override val path: Uri =
            effectsUri.buildUpon().appendPath("Vampire").build()
        override val nameRes: Int = R.string.mask_effect_vampire
        override val iconRes: Int = R.drawable.ic_effect_placeholder
        override val colorRes: Int = R.color.cyan

        override fun provide() = MaskDrawable(path)
    }

}