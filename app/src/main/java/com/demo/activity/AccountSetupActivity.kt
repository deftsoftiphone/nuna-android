package com.demo.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.databinding.ActivityAccountHandlerBinding
import com.demo.databinding.ActivityAccountSetupBinding

class AccountSetupActivity : BaseActivity() {

    lateinit var mBinding : ActivityAccountSetupBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_setup)
        navController = setNavigationController()
    }

    override fun setLang(strLang: String) {
    }

    private fun setNavigationController() : NavController {
        val navController = Navigation.findNavController(this, R.id.main_nav_setup_fragment)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard()}
        return navController
    }
}
