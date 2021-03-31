package com.demo.create_post.multiple

import android.net.Uri
import android.widget.ImageView
import com.demo.util.RoundedCornersTransformation
import com.squareup.picasso.Picasso

/**
 * Created by sangc on 2015-11-06.
 */
class ImageController(private val imgMain: ImageView) {

    fun setImgMain(path: Uri) {

        val transformation = RoundedCornersTransformation(20, 0)

        Picasso
            .get()
            .load(path).transform(transformation)
            .fit()
            .centerCrop()
            .into(imgMain)
    }
}