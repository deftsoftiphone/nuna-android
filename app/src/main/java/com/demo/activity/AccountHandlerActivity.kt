package com.demo.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.base.MainApplication
import com.demo.base.getCurrentFragment
import com.demo.databinding.ActivityAccountHandlerBinding
import com.demo.edit_profile.EditProfileFragment
import com.demo.util.Prefs
import com.demo.util.Validator

class AccountHandlerActivity : BaseActivity() {

    lateinit var mBinding: ActivityAccountHandlerBinding
    lateinit var navController: NavController
    private var closeApp = false
    private lateinit var myApp: MainApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        mBinding = setContentView(this, R.layout.activity_account_handler)
        navController = setNavigationController()

        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height: Int = displayMetrics.heightPixels
        val width: Int = displayMetrics.widthPixels
        var density = resources?.displayMetrics?.density
        val dWidth = (width / (density!! * 2))
        Log.e("DisplayWidth", "w${dWidth}")
        Prefs.init().deviceDisplayWidth = dWidth.toString()
        readArguments()
        initMyApp()
    }

    private fun initMyApp() {
        myApp = applicationContext as MainApplication
        myApp.setCurrentActivity(this)
    }

    private fun clearReferences() {
        val currActivity: Activity? = myApp.getCurrentActivity()
//        if (this == currActivity) myApp.setCurrentActivity(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        clearReferences()
    }

    private fun readArguments() {
//        Handler(mainLooper).postDelayed({
//            val msg = intent.getStringExtra(getString(R.string.key_user_blocked))
//            msg?.let {
//                if (it == getString(R.string.account_blocked_by_admin))
//                    Validator.showMessage(getString(R.string.account_blocked_by_admin))
//            }
//        }, 500)
    }

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    private fun setNavigationController(): NavController {
        val navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        navController.addOnDestinationChangedListener { _, _, _ ->  }
        return navController
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val f = getCurrentFragment(BaseFragment::class.java)
        f?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            if (closeApp)
                super.onBackPressed()
            startTimerToCloseApp()
        }
    }


    override fun setLang(strLang: String) {
    }

    private fun startTimerToCloseApp() {
        closeApp = true
        Validator.showMessage(getString(R.string.close_app))
        Handler().postDelayed({
            closeApp = false
        }, 3000)
    }
}


