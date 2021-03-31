package com.demo.viewPost.callback

import android.content.Context

/**
 * Created by Maroof Ahmed Siddique
 * Mindcrew Technologies Pvt Ltd
 */
interface CallbackActivity {

    fun showLoading(message: String?)
    fun hideLoading()
    fun hideKeyboard()
    fun showKeyboard()
    fun slideTopToBottom()
    fun slideBottomToTop()
    fun slideLeftToRight()
    fun innToOut()
    fun slideRightToLeft()
    fun openActivity(
        packageContext: Context?,
        cls: Class<*>?,
        isFinish: Boolean,
        isFinishAffinity: Boolean
    )

    fun vibrate()
}