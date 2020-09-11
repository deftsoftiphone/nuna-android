package com.demo.followPopularUsers

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.demo.R
import com.demo.base.MainApplication

enum class FollowStatus(val status : Int) {

    FOLLOWED(1) {
        override var btnBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.bg_white_cornered) as Drawable
        override var parentBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.bg_blue_gradient) as Drawable
        override var btnTxtColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.black)
        override var tvTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.white)
        override var tvSubTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.gray_view_color)
        override var btnTxt: Int = (R.string.unfollow)
    },

    UNFOLLOWED(2){
        override var btnBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.common_active_shape) as Drawable
        override var parentBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.bg_white_cornered) as Drawable
        override var btnTxtColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.white)
        override var tvTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.black)
        override var tvSubTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.grey)
        override var btnTxt: Int = R.string.follow
    },

    STATUS_PENDING(3){
        override var btnBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.common_disable_shape) as Drawable
        override var parentBg: Drawable = ContextCompat.getDrawable(MainApplication.get().applicationContext,R.drawable.bg_blue_gradient) as Drawable
        override var btnTxtColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.black)
        override var tvTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.white)
        override var tvSubTitleColor: Int = ContextCompat.getColor(MainApplication.get().applicationContext,R.color.white)
        override var btnTxt: Int =(R.string.unfollow)
    };


    abstract var btnBg : Drawable
    abstract var parentBg : Drawable
    abstract var btnTxtColor : Int
    abstract var tvTitleColor : Int
    abstract var tvSubTitleColor : Int
    abstract var btnTxt : Int
}