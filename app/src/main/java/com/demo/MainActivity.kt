package com.demo

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.demo.base.BaseActivity
import com.demo.databinding.ActivityMainBinding
import com.demo.databinding.CommonToolbarBinding
import com.demo.databinding.LayoutToolbarBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    NavController.OnDestinationChangedListener {
    override fun setupToolBar(toolbarBinding: LayoutToolbarBinding, showBack: Boolean, title: String?) {

    }



    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
      return true
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {

    }

    lateinit var mBinding: ActivityMainBinding
    lateinit var navController: NavController

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.DashboardSearchFragment -> {
                //mBinding.message.setText(R.string.title_ar)
                return@OnNavigationItemSelectedListener true
            }
            R.id.FollwingTabFragment -> {
               // mBinding.message.setText(R.string.title_location)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
              //  mBinding.message.setText(R.string.title_filter)
                return@OnNavigationItemSelectedListener true
            }
            R.id.UpdatedsFragment -> {
               // mBinding.message.setText(R.string.title_message)
                return@OnNavigationItemSelectedListener true
            }
            R.id.ProfileFragment -> {
             //   mBinding.message.setText(R.string.title_setting)
                return@OnNavigationItemSelectedListener true
            }

        }
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.navView.clearAnimation()
        navController = setNavigationController()
    }

    override fun setLang(strLang: String) {
    }

    private fun setNavigationController(): NavController {
        val navController = Navigation.findNavController(this, R.id.main_dash_fragment)
        navController.addOnDestinationChangedListener(this)
        mBinding.navView.setupWithNavController(navController)
        mBinding.navView.setOnNavigationItemSelectedListener(this)
        navController.addOnDestinationChangedListener { _, _, _ -> hideKeyboard() }
        return navController
    }


}
