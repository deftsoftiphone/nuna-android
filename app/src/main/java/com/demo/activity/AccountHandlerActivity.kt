package com.demo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.base.getCurrentFragment
import com.demo.databinding.ActivityAccountHandlerBinding
import com.demo.edit_profile.EditProfileFragment
import com.demo.util.Prefs

class AccountHandlerActivity : BaseActivity() {

    lateinit var mBinding: ActivityAccountHandlerBinding
    lateinit var navController: NavController

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
    }

    private fun setNavigationController(): NavController {
        val navController = Navigation.findNavController(this, R.id.main_nav_fragment)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }
        return navController
    }


    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val f = getCurrentFragment(EditProfileFragment::class.java)
        f?.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    override fun setLang(strLang: String) {
    }
}


