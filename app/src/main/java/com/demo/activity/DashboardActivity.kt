package com.demo.activity

//import com.sangcomz.fishbun.FishBun
//import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
//import com.sangcomz.fishbun.define.Define
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.banuba.sdk.cameraui.domain.MODE_RECORD_VIDEO
import com.banuba.sdk.ve.flow.VideoCreationActivity
import com.banuba.sdk.veui.ui.EXTRA_EXPORTED_SUCCESS
import com.banuba.sdk.veui.ui.ExportResult
import com.demo.R
import com.demo.base.BaseActivity
import com.demo.base.BaseFragment
import com.demo.base.MainApplication
import com.demo.base.getCurrentFragment
import com.demo.create_post.CreatePostActivity
import com.demo.databinding.ActivityMainBinding
import com.demo.marveleditor.VIdeoEditerActivity
import com.demo.model.response.baseResponse.DataHolder
import com.demo.model.response.baseResponse.NotificationData
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.util.*
import com.demo.viewPost.home.ViewPostActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jaiselrahman.filepicker.activity.FilePickerActivity
import com.jaiselrahman.filepicker.model.MediaFile
import kotlinx.android.synthetic.main.layout_badge_count.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

const val EDITIMAGE = 555
const val EDITVIDEO = 121
const val FILE_REQUEST_CODE = 111

class DashboardActivity : BaseActivity(), KodeinAware,
    BottomNavigationView.OnNavigationItemSelectedListener,
    NavController.OnDestinationChangedListener {
    val mClickHandler by lazy { ClickHandler() }
    var path = ArrayList<Uri>()
    var pathVideo = ArrayList<MediaFile>()
    lateinit var mBinding: ActivityMainBinding
    lateinit var navController: NavController
    private var closeApp = false
    override val kodein: Kodein by closestKodein()
    private lateinit var mViewModal: DashboardActivityViewModal
    private val viewModalFactory: DashboardActivityViewModalFactory by instance()
    private lateinit var myApp: MainApplication
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (controller.currentDestination?.id == R.id.DashboardSearchFragment || controller.currentDestination?.id == R.id.FollwingTabFragment
            || controller.currentDestination?.id == R.id.UpdatedsFragment || controller.currentDestination?.id == R.id.ProfileFragment
        ) {
            mBinding.parentLayoutBotom.visibility = VISIBLE
            mBinding.add.visibility = VISIBLE
        } else {
            mBinding.parentLayoutBotom.visibility = GONE
            mBinding.add.visibility = GONE
        }
        hideKeyboard()
        changeIcons(controller.currentDestination?.id)
        setupClickGuard()
    }


    private fun setupClickGuard() {
        mBinding.apply {
            bDashboard.setOnClickListener { clickHandler!!.onClickDashboard() }
            bHashtag.setOnClickListener { clickHandler!!.onClickHashtag() }
            ivAdd.setOnClickListener { clickHandler!!.onClickAdd() }
            bNotifications.setOnClickListener { clickHandler!!.onClickNotifications() }
            bProfile.setOnClickListener { clickHandler!!.onClickProfile() }

            ClickGuard.guard(
                bDashboard, bHashtag, ivAdd, bNotifications, bProfile
            )
        }
    }

    private fun initMyApp() {
        myApp = applicationContext as MainApplication
        myApp.setCurrentActivity(this)
    }

    private fun setupViewModal() {
        mViewModal =
            ViewModelProvider(this, viewModalFactory).get(DashboardActivityViewModal::class.java)
    }

    fun updateNotificationCount() {
        mViewModal.getNotificationCount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        setupViewModal()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.clickHandler = mClickHandler
        navController = Navigation.findNavController(this, R.id.main_dash_fragment)
        navController.addOnDestinationChangedListener(this)
//        navController = setNavigationController()
        openUserProfile(intent)
        openHashTag()
        setupObserver()
        EventBus.getDefault().register(this)
//        subscribeToNotification()
    }

    override fun onResume() {
        super.onResume()
        readNotificationArguments()
        updateNotificationCount()
        initMyApp()
    }


    private fun readNotificationArguments() {
        if (Util.checkIfHasNetwork()) {
            val fromNotification = intent.getStringExtra(getString(R.string.key_notification_type))
            val activityId: String? = intent.getStringExtra(getString(R.string.key_activity_id))
            val userId: String? = intent.getStringExtra(getString(R.string.key_user_id))
            val postId: String? = intent.getStringExtra(getString(R.string.key_post_id))

            if (!TextUtils.isEmpty(fromNotification)) {
                activityId?.let { mViewModal.readNotification(it) }
                when (fromNotification) {
                    getString(R.string.key_notification_type_post), getString(R.string.key_notification_type_admin) -> {
                        postId?.let { mViewModal.getPostDetail(it) }
                    }
                    getString(R.string.key_notification_type_user) -> {
                        userId?.let { mClickHandler.openUser(it) }
                    }
                    else -> navController.navigate(R.id.UpdatedsFragment)
                }
                intent = Intent()
            } else {
                val data = intent.extras?.get("data")?.toString()
                if (!TextUtils.isEmpty(data)) {
                    val notificationData = Gson().fromJson(data, NotificationData::class.java)
                    notificationData.activityId?.let { mViewModal.readNotification(it) }
                    when (notificationData.type) {
                        getString(R.string.key_notification_type_user) -> {
                            notificationData.userId?.let { mClickHandler.openUser(it) }
                        }
                        getString(R.string.key_notification_type_post), getString(R.string.key_notification_type_admin) -> {
                            notificationData.postId?.let { mViewModal.getPostDetail(it) }
                        }
                        else -> navController.navigate(R.id.UpdatedsFragment)
                    }
                    intent = Intent()
                }

            }
        } else Validator.showMessage(getString(R.string.connectErr))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onNotificationReceived(bundle: Bundle) {
        EventBus.getDefault().removeStickyEvent(bundle)
        mViewModal.getNotificationCount()
    }

    private fun setupObserver() {
        mViewModal.notificationCount.observe(this, {
            if (it > 0)
                mBinding.iBadge.tvNotificationCount.text = it.toString()
            Prefs.init().notificationCount = it
            mBinding.iBadge.visibility = if (it > 0) VISIBLE else GONE
        })

        mViewModal.showLoading.observe(this, {
            if (it) showProgressDialog() else hideProgressDialog()
        })

        mViewModal.toastMessage.observe(this, {
            if (!TextUtils.isEmpty(it)) Validator.showMessage(it)
        })

        mViewModal.postFromNotification.observe(this, { post ->
            post.id?.let {
                mClickHandler.openPost(post)
                mViewModal.postFromNotification.postValue(PostAssociated())
            }
        })
    }

    private fun openUserProfile(i: Intent) {
        val from = i.getStringExtra(getString(R.string.intent_key_from))
        if (!TextUtils.isEmpty(from)) {
            val id = i.getStringExtra(getString(R.string.intent_key_user_id))
            val userId = Prefs.init().currentUser?.id
            if (id == userId)
                runWithDelay(500) {
                    navController.navigate(
                        R.id.ProfileFragment,
                        bundleOf(
                            getString(R.string.intent_key_from) to from
                        )
                    )
                }
            else
                navController.navigate(
                    R.id.OthersProfileFragment,
                    bundleOf(
                        ParcelKeys.OTHER_USER_ID to id,
                        getString(R.string.intent_key_from) to from
                    )
                )
        }
    }

    private fun openHashTag() {
        val from = intent.getStringExtra(getString(R.string.intent_key_from))
        if (!TextUtils.isEmpty(from) && from == getString(R.string.intent_key_hashtag)) {
            val id = intent.getStringExtra(getString(R.string.intent_key_hashtag))
            id?.let {
                try {
                    navController.navigate(
                        R.id.allHashTagPostsFragment, bundleOf(
                            ParcelKeys.SELECTED_HASHTAG_ID to id,
                            getString(R.string.intent_key_from) to from
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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
            mBinding.vBottomShadow.visibility = VISIBLE
            bottomNav.setBackgroundColor(resources?.getColor(R.color.white)!!)
        }

        when (id) {
            R.id.DashboardSearchFragment -> {

                mBinding.apply {
                    vBottomShadow.visibility = View.GONE
//                    navController.navigate(id)
                    gradientLayoutBottom.visibility = VISIBLE
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
                NotificationManagerCompat.from(this@DashboardActivity).cancelAll()
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
            if (visibility == VISIBLE && alpha == 1f) {
                animate()
                    .alpha(0f)
                    .withEndAction { visibility = View.GONE }
                    .duration = 300
            }
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun showBottomNavigation() {
        mBinding.add.visibility = VISIBLE
        mBinding.vBottomShadow.visibility = VISIBLE
        // bottom_navigation is BottomNavigationView
        with(mBinding.navView) {
            visibility = VISIBLE
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
            /*    Define.ALBUM_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) { // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<String>) on <0.6.2
                    if (data != null) {
                        path = data.getParcelableArrayListExtra(Define.INTENT_PATH)!!

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


                        *//* val intent: Intent

                     intent = Intent(this, EditImageActivity::class.java)
                     intent.putExtra("list", path)
                     startActivityForResult(intent,EDITIMAGE)*//*
                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }*/
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

            EDITVIDEO -> if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val result =
                        data.getParcelableExtra(EXTRA_EXPORTED_SUCCESS) as? ExportResult.Success
                    val videoPath =
                        result?.videoList?.takeIf { it.isNotEmpty() }?.get(0)?.fileUri?.path
                    val thumbnailPath = result?.preview?.path

                    println("data = ${data}")

                    val bundle =
                        bundleOf(
                            getString(R.string.result_video_file_path) to videoPath,
                            getString(R.string.result_video_thumbnail_file_path) to thumbnailPath
                        )
                    Handler().postDelayed({
                        startActivityForResult(
                            Intent(this, CreatePostActivity::class.java).putExtra(
                                getString(R.string.create_post_arguments),
                                bundle
                            ), resources?.getInteger(R.integer.create_post_request_code)!!
                        )
//                        runOnUiThread {
//                            navController.navigate(R.id.CreatePostFragment, bundle)
//                        }
                    }, 100)


                }
                // you can get an image path(ArrayList<Uri>) on 0.6.2 and later

            }

            resources?.getInteger(R.integer.create_post_request_code)!! -> if (resultCode == RESULT_OK) {
                openUserProfile(data!!)
            }

            FILE_REQUEST_CODE ->

                if (data != null) {
                    pathVideo = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES)!!
                    intent = Intent(this, VIdeoEditerActivity::class.java)
                    intent.putExtra("path", pathVideo.get(0).uri)
                    startActivityForResult(intent, EDITVIDEO)
                }
        }

    }

    inner class ClickHandler {
        fun openUser(id: String) {
            navController.navigate(
                R.id.OthersProfileFragment,
                bundleOf(ParcelKeys.OTHER_USER_ID to id)
            )
        }

        fun openPost(post: PostAssociated) {
            if (Util.checkIfHasNetwork()) {
                EventBus.getDefault().postSticky(Bundle())
                val bundle = Bundle()
                val temp = ArrayList<PostAssociated>()
                temp.add(post)
                DataHolder.data = temp
                bundle.putInt(getString(R.string.intent_key_post_position), 0)
                startActivity(
                    Intent(
                        this@DashboardActivity,
                        ViewPostActivity::class.java
                    ).putExtra(getString(R.string.intent_key_show_post), bundle)
                )
            } else
                Validator.showMessage(getString(R.string.connectErr))
        }

        fun onClickAdd() {
            startActivityForResult(
                VideoCreationActivity.buildIntent(
                    applicationContext,
                    MODE_RECORD_VIDEO
                ), EDITVIDEO
            )
        }

        fun onClickDashboard() {
            navController.navigate(R.id.DashboardSearchFragment)
            mViewModal.getNotificationCount()

//            changeIcons(R.id.DashboardSearchFragment)
        }

        fun onClickHashtag() {
            navController.navigate(R.id.FollwingTabFragment)
            mViewModal.getNotificationCount()
//            changeIcons(R.id.FollwingTabFragment)
        }

        fun onClickNotifications() {
            navController.navigate(R.id.UpdatedsFragment)
            mViewModal.getNotificationCount()
//            changeIcons(R.id.UpdatedsFragment)
        }

        fun onClickProfile() {
            navController.navigate(R.id.ProfileFragment)
            mViewModal.getNotificationCount()
        }


        fun onSelectMedia(media: Media, dialog: BottomSheetDialog) {
            dialog.dismiss()
            if (media == Media.IMAGE) {
                /*  FishBun.with(this@DashboardActivity)
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
  */
            } else {
                startActivityForResult(
                    VideoCreationActivity.buildIntent(
                        applicationContext,
                        MODE_RECORD_VIDEO
                    ), EDITVIDEO
                )
            }

            // mViewModel.requestSaveUserProfile.get()!!.gender = gender.toString()
            mBinding.invalidateAll()
        }

    }

    override fun setLang(strLang: String) {

    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.DashboardSearchFragment) {
            if (activityStackIsEmpty()) {
                if (closeApp) {
                    super.onBackPressed()
                } else {
                    startTimerToCloseApp()
                }
            } else {
                super.onBackPressed()
            }
        } else {
            if (setOnBackActionListener == null || !setOnBackActionListener!!.onActionBack()) {
                if (navController.currentDestination?.id == R.id.UpdatedsFragment) {
                    if (!navController.popBackStack(R.id.DashboardSearchFragment, false)) {
                        navController.navigate(R.id.action_UpdatedsFragment_to_DashboardSearchFragment)
                        changeIcons(R.id.DashboardSearchFragment)
                    }
                } else if (navController.currentDestination?.id == R.id.FollwingTabFragment) {
                    if (!navController.popBackStack(R.id.DashboardSearchFragment, false)) {
                        navController.navigate(R.id.action_FollwingTabFragment_to_DashboardSearchFragment)
                        changeIcons(R.id.DashboardSearchFragment)
                    }
                } else if (navController.currentDestination?.id == R.id.ProfileFragment) {
                    if (!navController.popBackStack(R.id.DashboardSearchFragment, false)) {
                        navController.navigate(R.id.action_ProfileFragment_to_DashboardSearchFragment)
                        changeIcons(R.id.DashboardSearchFragment)
                    }
                } else {
                    if (!navController.popBackStack()) {
                        if (activityStackIsEmpty()) {
                            if (closeApp) {
                                super.onBackPressed()
                            } else {
                                startTimerToCloseApp()
                            }
                        } else {
                            super.onBackPressed()
                        }
                    }
                }
            }
        }
        /*else if (navController.currentDestination?.id == R.id.DashboardSearchFragment) {
            if (closeApp)
                finish()
            startTimerToCloseApp()
        } else if (!navController.navigateUp()) {
            if (closeApp)
                finish()
            startTimerToCloseApp()
        }*/
    }

    private fun activityStackIsEmpty(): Boolean {
        val activityManager =
            applicationContext.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val tasks = activityManager.getRunningTasks(10)
        return tasks.get(index = 0).numActivities == 1 &&
                tasks[0].topActivity?.className.equals(this@DashboardActivity::class.java.name)
    }

    private fun startTimerToCloseApp() {
        closeApp = true
        Validator.showMessage(getString(R.string.close_app))
        Handler().postDelayed({
            closeApp = false
        }, 3000)
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

    fun View.showView(show: Boolean) {
        this.animate()
            .alpha(if (show) 1.0f else 0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@showView.visibility = if (show) VISIBLE else GONE
                }
            });
    }
}
