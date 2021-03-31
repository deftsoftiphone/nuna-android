package com.demo.viewPost.utils

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.ImageViewTarget

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
class GifDrawableImageViewTarget(view: AppCompatImageView?, loopCount: Int) :
    ImageViewTarget<Drawable?>(view) {
    private var mLoopCount = GifDrawable.LOOP_FOREVER
    override fun setResource(resource: Drawable?) {
        if (resource is GifDrawable) {
            resource.setLoopCount(mLoopCount)
        }
        view.setImageDrawable(resource)
    }

    init {
        mLoopCount = loopCount
    }
}