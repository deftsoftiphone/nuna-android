package com.demo.dashboard_search.categoryFilter

import android.graphics.drawable.Drawable
import com.demo.R
import com.demo.util.Util

enum class CategoryFilterStatus{
    SELECTED_NEAR{
        override var parentBg: Int = R.drawable.bg_chip_red_gradient_cornered
        override var textColor: Int = Util.getColor(R.color.white)
    },
    SELECTED_NORMAL{
        override var parentBg: Int =  R.drawable.bg_chip_blue_gradient_cornered
        override var textColor: Int = Util.getColor(R.color.white)

    },
    UNSELECTED{
        override var parentBg: Int =  R.drawable.bg_chip_white_cornered
        override var textColor: Int = Util.getColor(R.color.black)

    };

    abstract var parentBg : Int
    abstract var textColor : Int

}