package com.demo.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseActivity
import com.demo.util.Prefs
import com.demo.util.Util
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.firebase.iid.FirebaseInstanceId

class SplashActivity : BaseActivity() {

    lateinit var mViewModel: SplashViewModel

    @SuppressLint("MissingPermission")
    private fun getMyPhoneNumber(): String? {
        val mTelephonyMgr: TelephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return mTelephonyMgr.line1Number
    }

    private fun getMy10DigitPhoneNumber(): String? {
        val s = getMyPhoneNumber()
        return if (s != null && s.length > 2) s.substring(2) else null
    }

    fun getPhoneNumb() {
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val intent = Credentials.getClient(this).getHintPickerIntent(hintRequest)

        startIntentSenderForResult(
            intent.intentSender, 505, null, 0, 0, 0, null
        )
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor()
        saveDeviceToken()
        setContentView(R.layout.activity_splash)

/*
        //
        val tMgr: TelephonyManager =
            getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val mPhoneNumber: String = tMgr. getLine1Number()
        getPhoneNumb();
        Log.e("SplashActivity ","mPhoneNumber $mPhoneNumber "+mPhoneNumber);
        Log.e("SplashActivity ","getMy10DigitPhoneNumber  "+getMy10DigitPhoneNumber());

*/

        val imageView = findViewById<ImageView>(R.id.imageView)

//        Glide.with(this)
//            .load(R.drawable.logo_animation)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .into(imageView)
//        GlideModule().registerComponents(this@SplashActivity, glide, Registry())


        Util.updateStatusBarColor("#FAFAFA", this as FragmentActivity)

        mViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        mViewModel.proceedAhead.observe(this, Observer {
            if (it) {
                val currentUser = Prefs.init().currentUser
                println("currentUser = ${currentUser}")
                val intent: Intent = if ( currentUser != null ) {
                    Intent(this, DashboardActivity::class.java)
                } else {
                    Intent(this, AccountHandlerActivity::class.java)

                }
                startActivity(intent)
                finish()
            }
        })

        Handler().postDelayed({
            mViewModel.proceedAhead.value = true
        }, 3500)
    }


    override fun setLang(strLang: String) {
    }

    private fun saveDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val deviceToken = instanceIdResult.token
            Log.e("device token", deviceToken)
            Log.e("device token length ", deviceToken.length.toString())

            Prefs.init().deviceToken = deviceToken

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 505) {
            data?.getParcelableExtra<Credential>(Credential.EXTRA_KEY)?.id?.let {
                // useFetchedPhoneNumber(it)
                Log.e("SplashActivity ", "Credential  " + it.toString());

            }
        }
    }


}
