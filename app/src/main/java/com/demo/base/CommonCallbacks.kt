package com.demo.base

import android.content.Context
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.demo.databinding.LayoutToolbarBinding


interface CommonCallbacks : AsyncViewController {

    fun setupToolBar(toolbarBinding: LayoutToolbarBinding, showBack: Boolean, title: String?)
//    fun setupCommonToolBar(toolbarBinding: CommonToolbarBinding, showBack: Boolean, title: String?)


    fun setupActionBarWithNavController(toolbar: Toolbar)

    fun hideKeyboard(v: View)

    fun requestFilePicker(actionType : Int)

    fun forceBack()

    fun isConnectedToNetwork(): Boolean

    fun isNetworkAvailable(): Boolean

    fun requestLogout()

    fun getSharedModel() : BaseActivityViewModel

    fun requestLocation()

    fun showMessage(msg: String)

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