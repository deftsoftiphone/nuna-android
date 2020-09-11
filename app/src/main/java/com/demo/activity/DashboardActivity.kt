package com.demo.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.base.getCurrentFragment
import com.demo.databinding.ActivityMainBinding
import com.demo.databinding.LayoutMediaBinding
import com.demo.marveleditor.VIdeoEditerActivity
import com.demo.photoeditor.EditImageActivity
import com.demo.util.Media
import com.github.nkzawa.socketio.client.IO
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.sangcomz.fishbun.define.Define
const val EDITIMAGE = 555
const val EDITVIDEO = 121
const val FILE_REQUEST_CODE = 111

class DashboardActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener,
    NavController.OnDestinationChangedListener {
    val mClickHandler by lazy { ClickHandler() }
    var isSecondNew = false

    var mCurrentLocation: Location? = null
    var path = ArrayList<Uri>()

    var pathVideo = ArrayList<MediaFile>()
    lateinit var mBinding: ActivityMainBinding
    lateinit var navController: NavController
    var showfilter: Boolean = true
    var socket: com.github.nkzawa.socketio.client.Socket? = null
    val topLevelFIds = listOf<Int>(
        R.id.DashboardSearchFragment,
        R.id.ProfileFragment,
        R.id.FollwingTabFragment,
        R.id.UpdatedsFragment,
        R.id.EditProfileFragment,
        R.id.EditProfileEntityFragment
    )


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {

        Log.e("DestinationChanged", "ondestination")

        if (controller.currentDestination?.id == R.id.EditProfileFragment || controller.currentDestination?.id == R.id.EditProfileEntityFragment
            || controller.currentDestination?.id == R.id.CreatePostFragment || controller.currentDestination?.id == R.id.allHashTagPostsFragment
            || controller.currentDestination?.id == R.id.particularNotificationFragment || controller.currentDestination?.id == R.id.SettingsFragment
            || controller.currentDestination?.id == R.id.LanguageSelectFragment || controller.currentDestination?.id == R.id.helpFragment
            || controller.currentDestination?.id == R.id.OthersProfileFragment || controller.currentDestination?.id == R.id.SearchFragment || controller.currentDestination?.id == R.id.FollowingFollowersFragment
        ) {
            mBinding.parentLayoutBotom.visibility = GONE
            mBinding.add.visibility = GONE
        } else {
            mBinding.parentLayoutBotom.visibility = VISIBLE
            mBinding.add.visibility = VISIBLE


        }
        hideKeyboard()

        changeIcons(controller.currentDestination?.id)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        socket = IO.socket(getString(R.string.base_url))
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.clickHandler = mClickHandler
        navController = Navigation.findNavController(this, R.id.main_dash_fragment)
        navController.addOnDestinationChangedListener(this)


//        navController = setNavigationController()

    }


    private fun setNavigationController(): NavController {
        val navController = Navigation.findNavController(this, R.id.main_dash_fragment)
        navController.addOnDestinationChangedListener(this)

        return navController
    }

    private fun changeIcons(id: Int?) {

        mBinding.apply {
            bDashboard.setImageResource(R.drawable.ic_home)
            bHashtag.setImageResource(R.drawable.ic_hashtag)
            bNotifications.setImageResource(R.drawable.ic_bell)
            bProfile.setImageResource(R.drawable.ic_user)
            gradientLayoutBottom.visibility = View.GONE
            mBinding.vBottomShadow.visibility = View.VISIBLE

            bottomNav.setBackgroundColor(resources?.getColor(R.color.white)!!)
        }



        when (id) {
            R.id.DashboardSearchFragment -> {

                mBinding.apply {
                    vBottomShadow.visibility = View.GONE
//                    navController.navigate(id)
                    gradientLayoutBottom.visibility = View.VISIBLE
                    bottomNav.setBackgroundColor(resources?.getColor(R.color.transparent)!!)
                    bDashboard.setImageResource(R.drawable.ic_home_white)
                }


            }
            R.id.FollwingTabFragment -> {
//                navController.navigate(id)
                mBinding.bHashtag.setImageResource(R.drawable.ic_hashtag_selected)

            }
            R.id.UpdatedsFragment -> {
//                navController.navigate(id)
                mBinding.bNotifications.setImageResource(R.drawable.ic_bell_selected)

            }


            R.id.particularNotificationFragment -> {
//                navController.navigate(id)
                mBinding.bNotifications.setImageResource(R.drawable.ic_bell_selected)

            }

            R.id.ProfileFragment -> {
//                navController.navigate(id)
                mBinding.bProfile.setImageResource(R.drawable.ic_user_selected)

            }

        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return false
    }

    private fun hideBottomNavigation() {
        mBinding.vBottomShadow.visibility = View.GONE
        // bottom_navigation is BottomNavigationView
        with(mBinding.navView) {
            if (visibility == View.VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
                    .duration = 300
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun showBottomNavigation() {
        mBinding.add.visibility = View.VISIBLE
        mBinding.vBottomShadow.visibility = View.VISIBLE
        // bottom_navigation is BottomNavigationView
        with(mBinding.navView) {
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .duration = 200
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /**
         * Which fragment you want send this result
         */
        val f = getCurrentFragment(BaseFragment::class.java)
        f?.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) { // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
// you can get an image path(ArrayList<String>) on <0.6.2
                if (data != null) {
                    path = data.getParcelableArrayListExtra(Define.INTENT_PATH)

                    if (path.size == 1) {
                        val intent: Intent

                        intent = Intent(this, EditImageActivity::class.java)
                        intent.putExtra("list", path)
                        startActivityForResult(intent, EDITIMAGE)
                    } else {

                        var bundle = bundleOf("strfile" to path)

                        Handler().postDelayed({
                            runOnUiThread {
                                navController.navigate(R.id.SubFragment, bundle)
                            }
                        }, 200)

                    }


                    /* val intent: Intent

                     intent = Intent(this, EditImageActivity::class.java)
                     intent.putExtra("list", path)
                     startActivityForResult(intent,EDITIMAGE)*/
                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }
        }

        when (requestCode) {
            EDITIMAGE -> if (resultCode == Activity.RESULT_OK) { // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
// you can get an image path(ArrayList<String>) on <0.6.2
                if (data != null) {
                    data.getStringExtra("strfile")


                    var bundle =
                        bundleOf("strfile" to data.getStringExtra("strfile"), "IsVideo" to false)

                    Handler().postDelayed({
                        runOnUiThread {
                            navController.navigate(R.id.CreatePostFragment, bundle)
                        }
                    }, 200)


                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }

            EDITVIDEO -> if (resultCode == Activity.RESULT_OK) { // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
// you can get an image path(ArrayList<String>) on <0.6.2
                if (data != null) {
                    data.getStringExtra("strfile")


                    var bundle =
                        bundleOf("strfile" to data.getStringExtra("strfile"), "IsVideo" to true)

                    Handler().postDelayed({
                        runOnUiThread {
                            navController.navigate(R.id.CreatePostFragment, bundle)
                        }
                    }, 200)


                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }
        }

        when (requestCode) {

            FILE_REQUEST_CODE ->

                if (data != null) {
                    pathVideo = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)
                    val intent: Intent

                    intent = Intent(this, VIdeoEditerActivity::class.java)
                    intent.putExtra("path", pathVideo.get(0).uri)
                    startActivityForResult(intent, EDITVIDEO)
                }


        }


    }

    inner class ClickHandler {

        fun onClickAdd() {
            val dialog = BottomSheetDialog(this@DashboardActivity)

            val binding =
                LayoutMediaBinding.inflate(LayoutInflater.from(this@DashboardActivity)).apply {
                    clickHandler = mClickHandler
                    this.dialog = dialog
                }

            dialog.setContentView(binding.root)
            dialog.show()
        }


        fun onClickDashboard() {
            navController.navigate(R.id.DashboardSearchFragment)
//            changeIcons(R.id.DashboardSearchFragment)
        }

        fun onClickHashtag() {
            navController.navigate(R.id.FollwingTabFragment)

//            changeIcons(R.id.FollwingTabFragment)
        }

        fun onClickNotifications() {
            navController.navigate(R.id.UpdatedsFragment)

//            changeIcons(R.id.UpdatedsFragment)
        }

        fun onClickProfile() {
            navController.navigate(R.id.ProfileFragment)
        }


        fun onSelectMedia(media: Media, dialog: BottomSheetDialog) {
            dialog.dismiss()

            if (media == Media.IMAGE) {
                FishBun.with(this@DashboardActivity)
                    .setImageAdapter(GlideAdapter())
                    .setIsUseDetailView(false)
                    .setMaxCount(1) //Deprecated
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setPickerSpanCount(2)
                    .setActionBarColor(
                        Color.parseColor("#ef8f90"),
                        Color.parseColor("#ef8f90"),
                        false
                    )
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    // .setArrayPaths(path)
                    // .setAlbumSpanCount(2, 4)
                    .setAlbumSpanCountOnlPortrait(2)
                    .setButtonInAlbumActivity(false)
                    .setCamera(true)
                    .setReachLimitAutomaticClose(true)
                    // .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this, R.drawable.ic_custom_back_white))
                    // .setOkButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_custom_ok))
                    .setAllViewTitle("All")
                    .setActionBarTitle("Picture select")
                    .textOnImagesSelectionLimitReached("Limit Reached!")
                    .textOnNothingSelected("Nothing Selected")
                    .setSelectCircleStrokeColor(Color.BLACK)
                    .isStartInAllView(false)
                    .startAlbum()

            } else {
                val intent: Intent

                intent = Intent(baseContext, VIdeoEditerActivity::class.java)

                startActivityForResult(intent, EDITVIDEO)
/*
                val intent: Intent = Intent(baseContext, FilePickerActivity::class.java)

                intent.putExtra(FilePickerActivity.CONFIGS,  Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(true)
                    .enableVideoCapture(true)
                .setMaxSelection(1)
                .setSkipZeroSizeFiles(true)
                .build())
                startActivityForResult(intent, FILE_REQUEST_CODE)*/


            }

            // mViewModel.requestSaveUserProfile.get()!!.gender = gender.toString()
            mBinding.invalidateAll()
        }

    }


    override fun setLang(strLang: String) {

    }


    override fun onBackPressed() {

        if (navController.currentDestination?.id == R.id.CreatePostFragment) {
            backConfirmationDialog()

        } else if (navController.currentDestination?.id == R.id.UpdatedsFragment || navController.currentDestination?.id == R.id.FollwingTabFragment || navController.currentDestination?.id == R.id.ProfileFragment) {
            navController.navigate(R.id.DashboardSearchFragment)
        } else if (navController.currentDestination?.id == R.id.DashboardSearchFragment) {
            finish()

        } else if (!navController.navigateUp()) {
            finish()
        }
    }

    private fun enableBottomNavItems() {
        mBinding.navView.menu.forEach { it.isEnabled = false }
        Handler().postDelayed({
            mBinding.navView.menu.forEach { it.isEnabled = true }
        }, 500)
    }

    private fun backConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.cancel_post))
        builder.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            dialog.dismiss()
            navController.navigateUp()
        }

        builder.setNegativeButton(
            "No"
        ) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    override fun onStop() {
        super.onStop()
        Log.e("OnStop", "Onstop")
    }


    fun View.showView(show: Boolean) {
        this.animate()
            .alpha(if (show) 1.0f else 0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@showView.setVisibility(if (show) VISIBLE else GONE)
                }
            });
    }

}
