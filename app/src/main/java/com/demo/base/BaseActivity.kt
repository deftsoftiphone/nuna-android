package com.demo.base

//import www.sanju.motiontoast.MotionToast
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.databinding.AppCustomDialogBinding
import com.demo.databinding.LayoutToolbarBinding
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.socketio.SocketIO
import com.demo.util.*
import com.demo.util.location.LocationManager
import com.demo.viewPost.connectivity.NetworkAvailability
import com.demo.webservice.ApiRegister
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File


abstract class BaseActivity : ScopedActivity(), CommonCallbacks, KodeinAware,
    OnLocaleChangedListener, ConnectionStateMonitor.OnNetworkAvailableCallbacks {
    override val kodein: Kodein by closestKodein()
    private val socketIO: SocketIO by instance()
    val resource: ResourcesProvider by instance()
    lateinit var mBaseViewModel: BaseActivityViewModel
    private var aD: AlertDialog? = null
    var setOnBackActionListener: SetOnBackActionListener? = null
    private val localizationDelegate =
        LocalizationActivityDelegate(this)
    var locationManager: LocationManager? = null
    var iPermissionGranted: IPermissionGranted? = null
    var isGranted = false
    var isSecond = false
    private lateinit var mMyApp: MainApplication
    private var vibrator: Vibrator? = null
//    private var connectionStateMonitor: ConnectionStateMonitor? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCenter.start(
            application, "059a3bc2-0e86-4b82-ada0-a46d987d4add",
            Analytics::class.java, Crashes::class.java
        )
        vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        initMyApp()
        setupBasics()
        this.selectLanguage(null)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
        // WindowManager.LayoutParams.FLAG_SECURE)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }

                Log.w("DynamicLink", "getDynamicLink:on Sucess ")
                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...

                // ...
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            AppLifecycleListener(
                this,
                socketIO,
                resource
            )
        )
//        socketListener()
    }

    private fun initMyApp() {
        try {
            mMyApp = applicationContext as MainApplication
            mMyApp.setCurrentActivity(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        localizationDelegate.onResume(this)
        socketIO.connect()
//        startListeningNetwork()
    }

    override fun onStop() {
        super.onStop()
//        stopListeningNetwork()
    }

    /*  private fun startListeningNetwork() {
          if (connectionStateMonitor == null)
              connectionStateMonitor = ConnectionStateMonitor(this, this)
          //Register
          connectionStateMonitor?.enable()

          // Recheck network status manually whenever activity resumes
          if (connectionStateMonitor?.hasNetworkConnection() == false) onNegative()
          else onPositive()
      }

      private fun stopListeningNetwork() {
          connectionStateMonitor?.disable()
          connectionStateMonitor = null
      }*/

    override fun onPositive() {
    }

    override fun onNegative() {
    }

    open fun invokeSocketEvent(method: String) {
        if (!socketIO.isConnected())
            socketIO.connect()

//        Handler(Looper.getMainLooper()).postDelayed({
        val userId = Prefs.init().currentUser?.id
        userId?.let {
            if (socketIO.isConnected()) {
                socketIO.emitMessage(
                    method,
                    toJsonObject(
                        "userId" to it,
                        "deviceToken" to getUniqueToken(),
                        "success" to "true"
                    )
                )
            }
        }
//        }, 5000)
    }

    open fun isNetworkConnected(): Boolean {
        return NetworkAvailability.checkNetworkStatus(this)
    }

    protected open fun setStatusBarTransparentFlag() {
        val decorView = window.decorView
        decorView.setOnApplyWindowInsetsListener { v: View, insets: WindowInsets? ->
            val defaultInsets = v.onApplyWindowInsets(insets)
            defaultInsets.replaceSystemWindowInsets(
                defaultInsets.systemWindowInsetLeft,
                0,
                defaultInsets.systemWindowInsetRight,
                defaultInsets.systemWindowInsetBottom
            )
        }
        ViewCompat.requestApplyInsets(decorView)
        window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
    }

    fun showInfoToast(message: String) {
//        MotionToast.createToast(
//            this, message,
//            MotionToast.TOAST_INFO,
//            MotionToast.GRAVITY_BOTTOM,
//            MotionToast.SHORT_DURATION, null
//        )
    }

    override fun onDestroy() {
        clearReferences()
        super.onDestroy()
    }

    private fun clearReferences() {
        val currActivity: Activity? = mMyApp.getCurrentActivity()
//        if (this == currActivity) mMyApp.setCurrentActivity(null)
    }

    fun setStatusBarColor() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources!!.getColor(R.color.status_bar_color)
    }

    private fun setupBasics() {
        mBaseViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(this as AsyncViewController))
                .get(BaseActivityViewModel::class.java)
        setObservers()
    }

    override fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    fun showProgress() {
        MyProgress.show( this.supportFragmentManager)
    }

    fun hideProgress() {
        MyProgress.hide(this.supportFragmentManager)
    }

    override fun showProgressDialog() {
        if (mBaseViewModel.progressDialogStatus.value == null || !mBaseViewModel.progressDialogStatus.value.equals(
                "_show"
            )
        ) {
            mBaseViewModel.progressDialogStatus.value = "_show"
        }
    }

    override fun hideProgressDialog() {
        if (mBaseViewModel.progressDialogStatus.value == null
            || !mBaseViewModel.progressDialogStatus.value.equals("_hide")
        ) {
            mBaseViewModel.progressDialogStatus.value = "_hide"
        }
    }

    /* override fun onSupportNavigateUp(): Boolean {
         onBackPressed()
         return true
     }

     override fun onBackPressed() {
         val bf = getCurrentFragment(BaseFragment::class.java)
         bf?.onFragBack()
         Log.e("BackPressesss","Back"+bf?.onFragBack())

 >>>>>>> Stashed changes
         if (bf?.onFragBack() == false) {
             Log.e("BackPressesssTrue","Back")
            showAppCloseDialog()
         }
     }
 */
    fun closeActivity() {
        finish()
    }

    private fun setObservers() {
        mBaseViewModel.keyboardController.observe(this, Observer {
            runOnUiThread {
                if (it) {
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                } else {
                    val imm =
                        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
            }
        })

        mBaseViewModel.progressDialogStatus.observe(this, Observer {
            it?.let {
                if (it == "_show") {
                    showProgress()
                } else if (it == "_hide") {
                    hideProgress()
                    Log.d("NFCT", "Dialog Hiding")
                }
            }
        })

        mBaseViewModel.alertDialogController.observe(this, Observer {
            it?.let {
                if (aD?.isShowing == true) aD?.dismiss()

                val builder = AlertDialog.Builder(this).setCancelable(false)

                val dialogBinding =
                    AppCustomDialogBinding.inflate(layoutInflater, null, false).apply {
                        mBaseViewModel.alertDialogSpecs.message = it
                        specs = mBaseViewModel.alertDialogSpecs
                    }
                builder.setView(dialogBinding.root)
                aD = builder.create()
                aD!!.window?.setBackgroundDrawableResource(R.drawable.bg_cornered)

                dialogBinding.btnYes.setOnClickListener {
                    mBaseViewModel.alertDialogSpecs.alertDialogBtnListener?.onClick(
                        aD,
                        DialogInterface.BUTTON_POSITIVE
                    )
                    aD?.dismiss()
                }
                dialogBinding.btnNo.setOnClickListener {
                    mBaseViewModel.alertDialogSpecs.alertDialogBtnListener?.onClick(
                        aD,
                        DialogInterface.BUTTON_NEGATIVE
                    )
                    aD?.dismiss()
                }

                aD!!.show()
                mBaseViewModel.alertDialogController.value = null
            }

        })

        mBaseViewModel.responseLogOut.observe(this, Observer {
            if (it.responseCode == 200) {
                onLogOutSuccess()
            }
        })
    }


    override fun setupToolBar(
        toolbarBinding: LayoutToolbarBinding,
        showBack: Boolean,
        title: String?
    ) {
        setSupportActionBar(toolbarBinding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayShowTitleEnabled(false)

        if (title?.isNotEmpty() == true) {
            toolbarBinding.tvTitle.text = title
        } else {
            toolbarBinding.tvTitle.text = ""
        }

        if (showBack) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            toolbarBinding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
    }

    override fun getSharedModel(): BaseActivityViewModel {
        return mBaseViewModel
    }

    override fun showAlertDialog(msg: String, btnListener: DialogInterface.OnClickListener?) {
        mBaseViewModel.alertDialogSpecs = AlertDialogSpecs()
        mBaseViewModel.alertDialogSpecs.alertDialogBtnListener = btnListener
        mBaseViewModel.alertDialogController.value = msg
    }

    override fun showAlertDialog(
        title: String,
        msg: String,
        btnPosTxt: String,
        btnNegTxt: String,
        btnListener: DialogInterface.OnClickListener?
    ) {
        mBaseViewModel.alertDialogSpecs = AlertDialogSpecs()
        mBaseViewModel.alertDialogSpecs.title = title
        mBaseViewModel.alertDialogSpecs.btnPos = btnPosTxt
        mBaseViewModel.alertDialogSpecs.btnNeg = btnNegTxt
        mBaseViewModel.alertDialogSpecs.alertDialogBtnListener = btnListener
        mBaseViewModel.alertDialogController.value = msg
    }

    override fun hideAlertDialog() {
        mBaseViewModel.alertDialogController.value = "null"
    }

    override fun hideKeyboard() {
        mBaseViewModel.keyboardController.value = false
    }

    override fun hideKeyboard(v: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun showKeyboard() {
        mBaseViewModel.keyboardController.value = true
    }

    override fun onNoInternet() {
        showAlertDialog(getString(R.string.no_network_connected), null)
    }

    override fun onApiCallFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        if (errCode == 403) {
            showAlertDialog(errorMessage, DialogInterface.OnClickListener { _, _ ->
                onLogOutSuccess()
            })
        } else if (errCode == 201) {
            if (apiUrl == "saveUserProfile") {
                showAlertDialog(
                    getString(R.string.usrname_exists),
                    DialogInterface.OnClickListener { _, _ ->

                    })
            }

            if (apiUrl == ApiRegister.GETPOSTLIST) {

                if (getCurrentFragment(BaseFragment::class.java)?.onApiRequestFailed(
                        apiUrl,
                        errCode,
                        errorMessage
                    ) == false
                ) {
                    //showAlertDialog(errorMessage, null)
                    //fragment has nothing to do with this failure, can put some logic here
                }

            }

            if (apiUrl == ApiRegister.GET_FOLLOWING_FOLLOWER_USERS_LIST) {
                if (getCurrentFragment(BaseFragment::class.java)?.onApiRequestFailed(
                        apiUrl, errCode, errorMessage
                    ) == false
                ) {
                }
            }


            if (apiUrl == ApiRegister.GET_USER_NOTIFICATION) {

                if (getCurrentFragment(BaseFragment::class.java)?.onApiRequestFailed(
                        apiUrl,
                        errCode,
                        errorMessage
                    ) == false
                ) {
                }

            }
            if (apiUrl.equals(ApiRegister.VALIDATEOTP)) {

                if (getCurrentFragment(BaseFragment::class.java)?.onApiRequestFailed(
                        apiUrl,
                        errCode,
                        errorMessage
                    ) == false
                ) {
                }

            }

        }
        return true
    }

    open fun onFilePicked(pickedFileUri: String) {
        getCurrentFragment(BaseFragment::class.java)?.onFilePicked(pickedFileUri)
    }

    override fun requestFilePicker(actionType: Int) {
    }


    override fun setupActionBarWithNavController(toolbar: Toolbar) {


    }

    override fun requestLogout() {
        showAlertDialog(
            "",
            getString(R.string.msg_app_logout_confirmation),
            getString(R.string.yes),
            getString(R.string.no),
            DialogInterface.OnClickListener { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // mBaseViewModel.logOut()
                    onLogOutSuccess()
                }
            })
    }

    private fun onLogOutSuccess() {
        Prefs.init().clear()
        startActivity(
            Intent(this, AccountHandlerActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }

    override fun forceBack() {
        super.onBackPressed()
    }


    private fun showAppCloseDialog() {

        if (!isSecond) {
            isSecond = true
        } else {
            finish()
        }


        /*  showAlertDialog(
              "",
              getString(R.string.msg_app_close),
              getString(R.string.yes),
              getString(R.string.no),
              DialogInterface.OnClickListener { _, _which ->
                  if (_which == DialogInterface.BUTTON_POSITIVE) {
                      finish()
                  }
              })*/
    }

    fun checkAndRequestPermissions(perms: Array<String>, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        val results = ArrayList<String>()
        for (s in perms) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    s
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                results.add(s)
            }
        }
        if (results.size == 0) {
            return true
        } else {
            val arr = arrayOfNulls<String>(results.size)
            requestPermission(results.toArray(arr), requestCode)
            return false
        }
    }

    override fun hasPermission(s: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                s
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun requestPermission(perms: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this@BaseActivity, perms, requestCode);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        /*if (requestCode == Constant.REQC_PICK_IMAGE) {
            if (Util.areAllPermissionsAccepted(grantResults)) {

            }
        } else
            */
        if (requestCode == AppRequestCode.REQUEST_LOCATION_PERMISSION) {

            if (isAllPermissionGranted(grantResults)) {
                setupLocation()
            } else {
                onReceiveLocation(null)
            }

        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (isAllPermissionGranted(grantResults))
            iPermissionGranted?.permissionGranted(requestCode)
        else {
            //
            var isPermission = true;
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@BaseActivity as Activity,
                        permission
                    )
                ) {

                    isPermission = false
                    Validator.showMessage(getString(R.string.permission_denied))
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            this@BaseActivity as Activity,
                            permission
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                    } else {
                        //  callPermissionSettings()
                        isPermission = false
                    }
                }
            }
            if (!isPermission)
                callPermissionSettings()
            //

        }
    }

    private fun callPermissionSettings() {

        ///-----------------
        val builder = android.app.AlertDialog.Builder(this@BaseActivity)
        builder.setTitle(getString(R.string.need_permission))
        builder.setMessage(getString(R.string.need_permission_msg))
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, which ->
            //   openSettings()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri =
                Uri.fromParts("package", this@BaseActivity!!.applicationContext.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 300)
        }

        builder.setNegativeButton(getString(R.string.Cancel)) { dialog, which ->

        }

        /* builder.setNeutralButton("Maybe") { dialog, which ->
             Toast.makeText(context,
                 "Maybe", Toast.LENGTH_SHORT).show()
         }*/
        builder.show()

    }

    override fun isConnectedToNetwork(): Boolean {
        val cm =
            MainApplication.get().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null

    }

    fun onReceiveLocation(newLocation: Location?) {
        val f = getCurrentFragment(BaseFragment::class.java)
        if (f?.onReceiveLocation(newLocation) == false) {

        }
    }

    override fun getResources(): Resources? {
        return localizationDelegate.getResources(super.getResources())
    }

    // Just override method locale change event
    override fun onBeforeLocaleChanged() {}

    override fun onAfterLocaleChanged() {}


    fun askForPermission(
        requestCode: Int,
        permissions: Array<String>,
        iPermissionGranted: IPermissionGranted?
    ) {

        if (hasPermissions(permissions)) {
            iPermissionGranted?.permissionGranted(requestCode)
        } else {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                requestCode
            )
        }
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        var isAllPermissionGranted = false
        for (item in permissions.iterator()) {
            isAllPermissionGranted = ActivityCompat.checkSelfPermission(this, item) ==
                    PackageManager.PERMISSION_GRANTED
            if (!isAllPermissionGranted)
                break
        }

        return isAllPermissionGranted
    }

    fun askForLocationPermission(iPermissionGranted: IPermissionGranted?) {
        askForPermission(
            AppRequestCode.REQUEST_LOCATION_PERMISSION,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), iPermissionGranted
        )
    }


    fun isAllPermissionGranted(grantResults: IntArray): Boolean {
        isGranted = false
        for (item in grantResults) {
            isGranted = item == PackageManager.PERMISSION_GRANTED
            if (!isGranted)
                break

        }
        return isGranted
    }

    override fun requestLocation() {
        if (checkAndRequestPermissions(
                AppRequestCode.PERMISSION_LOCATION,
                AppRequestCode.REQUEST_LOCATION_PERMISSION
            )
        ) {
            setupLocation()
        }
    }

    fun setupLocation() {
        if (locationManager == null) {
            locationManager = LocationManager(this)
            subscribeLocationUpdate()
            locationManager?.getFusedClient()
        }
    }

    private fun subscribeLocationUpdate() {
        locationManager?.mLocationResponse?.observe(
            this,
            Observer { location ->
                location.let {
                    onReceiveLocation(it)
                }
            })
    }

    fun handleAPIError(msg: String) {
        hideProgressDialog()
        Log.e("ERROR", msg)
        Validator.showMessage(msg)
//        Validator.showCustomToast(getString(R.string.something_went_wrong))
    }

    override fun slideTopToBottom() {
        overridePendingTransition(R.anim.enter_top, R.anim.exit_bottom)
    }

    override fun slideBottomToTop() {
        overridePendingTransition(R.anim.enter_bottom, R.anim.exit_top)
    }

    override fun slideLeftToRight() {
        overridePendingTransition(R.anim.enter_left, R.anim.exit_right)
    }

    override fun innToOut() {
        overridePendingTransition(R.anim.dialog_in, R.anim.dialog_out)
    }

    override fun slideRightToLeft() {
        overridePendingTransition(R.anim.enter_right, R.anim.exit_left)
    }

    override fun openActivity(
        packageContext: Context?,
        cls: Class<*>?,
        isFinish: Boolean,
        isFinishAffinity: Boolean
    ) {
        val intent = Intent(packageContext, cls)
        startActivity(intent)
        if (isFinish) {
            finish()
        } else if (isFinishAffinity) {
            finishAffinity()
        }
    }

    override fun vibrate() {
        vibrator!!.vibrate(100)
    }

    fun getUriFromFile(file: File): Uri {
        val apkURI: Uri = FileProvider.getUriForFile(
            this, applicationContext
                .packageName.toString() + ".provider", file
        )
        return apkURI
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun showMessage(msg: String) {
        Validator.showMessage(msg)
    }

    interface SetOnBackActionListener {
        fun onActionBack(): Boolean
    }


}