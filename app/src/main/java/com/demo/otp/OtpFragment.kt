package com.demo.otp

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentOtpBinding
import com.demo.model.request.RequestUserRegister
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.socketio.SocketIO
import com.demo.receivers.SMSListener
import com.demo.receivers.SmsBroadcastReceiverListener
import com.demo.util.*
import com.demo.viewPost.utils.KeyboardHeightProvider
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.auth.api.phone.SmsRetriever
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


class OtpFragment : BaseFragment(), KodeinAware,
    KeyboardHeightProvider.KeyboardHeightObserver
//    ,OTPReceiveInterface
{
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: OtpViewModelFactory by instance()
    private val socketIO: SocketIO by instance()
    private val resources: ResourcesProvider by instance()
    private val mySMSBroadcastReceiver = SMSListener()
    private lateinit var smsListener: SmsBroadcastReceiverListener
    private var keyboardListener: Unregistrar? = null
    private var onConnectError: Emitter.Listener? = null
    private lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private lateinit var smsBroadcastReceiver: SmsReceiver
    private val smsIntentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
    private val REQ_USER_CONSENT = 200

    //    private val timer: GlobalTimer by instance()
    private var timer: CountDownTimer? = null

    /* override fun onOtpReceived(otp: String) {
         //tvOtp.text = "OTP is : $otp"
         mBinding.pvOTP.setText(otp)
     }

     override fun onOtpTimeout() {
         //tvOtp.text = "Time out, please resend"
     }*/

    lateinit var mBinding: FragmentOtpBinding
    lateinit var mViewModel: OtpViewModel

    override fun onResume() {
        super.onResume()
        mBinding.pvOTP.requestFocus()

        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(mBinding.pvOTP, InputMethodManager.SHOW_FORCED)
        socketIO.connect()

        if (!::smsBroadcastReceiver.isInitialized)
            registerOTPReceiver()
    }


    override fun onStop() {
        super.onStop()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
//        socketIO.disconnect()
    }

    private fun keyboardShowListener() {
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(
            activity,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
//                    if (isOpen) removeConstraint() else addConstraint()
//                    requireActivity().window.setSoftInputMode(
//                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                    )
                }
            })
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.setKeyboardHeightObserver(null)
    }

    override fun onDestroy() {
        SMSListener.unbindListener()
        super.onDestroy()
        unregisterOTPReceiver()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set otp callback from broadcast receiver
//        mySMSBroadcastReceiver.setOnOtpListeners(this)

        //Registering Receiver

        /* val intentFilter = IntentFilter()
         intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
         activity?.applicationContext?.registerReceiver(mySMSBroadcastReceiver, intentFilter)*/
        setupViewModel()
        mViewModel.parseBundle(arguments)
//        setupSMSListener()
//        initSmsReceiver()
    }


    private fun setupSMSListener() {
        /* SMSListener.bindListener(object : SmsBroadcastReceiverListener {
             override fun onSuccess(message: String?) {
                 message?.let {
                     mBinding.pvOTP.setText(mViewModel.getOTP(message))
                 }
             }

             override fun onFailure() {

             }
         })*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentOtpBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        keyboardHeightProvider = KeyboardHeightProvider(requireActivity())
        mBinding.root.post {
            keyboardHeightProvider.start()
        }
        keyboardHeightProvider.setKeyboardHeightObserver(this)
        mBinding.lifecycleOwner = viewLifecycleOwner
        (requireActivity() as AccountHandlerActivity).apply {
            setStatusBarColor()
        }
        mBinding.pvOTP.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 4) {
                    ClickHandler().onClickOTPSend()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        startTimer()
        readArguments()
        setupSocketListeners()
        requireActivity().updateLanguage()
        setupSmsListenerRequest()
        registerOTPReceiver()
        return mBinding.root
    }

    private fun readArguments() {
        val mobileNo = arguments?.getString(ParcelKeys.PHONENUMBER)
        val countryCode = arguments?.getString(ParcelKeys.COUNTRYCODE)

        mBinding.tvVerificationNumber.text = getString(R.string.otp_sent).replace(
            "[number]",
            " $countryCode $mobileNo"
        )
        mViewModel.requestRegister.apply {
            set(
                RequestUserRegister(
                    mobileNo = mobileNo,
                    countryCode = countryCode
                )
            )
        }
    }

    private fun setupSocketListeners() {
        socketIO.listenOn(
            Socket.EVENT_CONNECT
        ) { args ->
            Log.d("EVENT CONNECTED", socketIO.isConnected().toString())
        }

        socketIO.listenOn(
            Socket.EVENT_CONNECT_ERROR
        ) { args -> Log.d("EVENT CONNECT ERROR", args[0].toString()) }


        socketIO.listenOn(getString(R.string.socket_listen_user_login)) { args ->
            if (args.isNotEmpty()) {
                Log.d("LOGIN SUCCESS", args[0].toString())
            }
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(OtpViewModel::class.java)
    }

    private fun verifyOTP() = launch {
        if (Util.checkIfHasNetwork()) {
            commonCallbacks?.showProgressDialog()
            mViewModel.verifyOTP().observe(viewLifecycleOwner, {
                commonCallbacks?.hideProgressDialog()
                if (it.error == null && it.success!!) {
                    if (it.user?.isDeactivated!!) {
                        handleAPIError(getString(R.string.account_blocked_by_admin))
                    } else {
                        stopTimer()
                        unregisterOTPReceiver()
//                        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener(socketIO, resources))
                        sendLoginStatusToSocket(it.user!!)
                        Prefs.init().currentUser = it.user
                        Prefs.init().currentToken = it.token!!
                        Prefs.init().notificationAll = true
                        startActivity(Intent(requireContext(), DashboardActivity::class.java))
                        (requireActivity() as AccountHandlerActivity).finish()
                    }
                } else {
                    if (it.error?.message == "Oops, something went wrong") {
                        handleAPIError(getString(R.string.account_blocked_by_admin))
                    } else {
                        handleAPIError(it.error!!.message!!)
                    }
                    mBinding.pvOTP.setText("")
                }
            })
        } else Validator.showMessage(getString(R.string.connectErr))
    }

//    user-logs-in => when user logsIn <= 9468105119login-success

    private fun sendLoginStatusToSocket(user: User) {
        if (socketIO.isConnected()) {
            socketIO.emitMessage(
                getString(R.string.socket_user_login),
                toJsonObject(
                    "userId" to "${user.id}",
                    "deviceToken" to requireContext().getUniqueToken(),
                    "success" to "true"
                )
            )
        }
    }

    private fun resendOTP() = launch {
        commonCallbacks?.showProgressDialog()
        mViewModel.resendOTP().observe(viewLifecycleOwner, Observer {
            commonCallbacks?.hideProgressDialog()
            if (it.error == null && it.success!!) {
                Validator.showMessage(getString(R.string.otpResendSuccess))
//                startTimer()
            } else {
                it.error?.message?.let { it1 -> handleAPIError(it1) }
            }
        })

        mViewModel.toastMessage.observe(viewLifecycleOwner, {
            if (!TextUtils.isEmpty(it))
                Validator.showMessage(it)
        })
    }

    inner class ClickHandler {
        fun backPress() {
            findNavController().navigateUp()
        }

        fun onClickOTPSend() {
            commonCallbacks?.hideKeyboard()
            if (!mViewModel.isOTPEmpty()) {
                mViewModel.requestOtp.get()!!.apply {
                    mobileNo = mViewModel.requestRegister.get()?.mobileNo
                    languageId = Prefs.init().selectedLang._id
                    countryCode = mViewModel.requestRegister.get()?.countryCode
                    authDeviceToken = requireContext().getUniqueToken()
                }

                if (mViewModel.isOTPValid()) verifyOTP()
                else handleAPIError(getString(R.string.invalidOtpErr))
            } else handleAPIError(getString(R.string.err_otp_missing))
        }

        fun onClickResentOTP() {
            mBinding.pvOTP.setText("")
            //mBinding.pinOtp.clearFocus()
//            commonCallbacks?.hideKeyboard()
            resendOTP()
            startTimer()
        }

        fun onClickForgotPassword() {
            commonCallbacks?.hideKeyboard()
            findNavController().navigate(R.id.ForgotPasswordFragment)
        }
    }

    companion object {
        val OTP_REGEX: String? = "[0-9]{1,6}"
    }

    override fun onApiRequestFailed(
        apiUrl: String,
        errCode: Int,
        errorMessage: String
    ): Boolean {
        // Validator.showCustomToast("dlfkkf"+errorMessage)
        Validator.showCustomToast(errorMessage)
        return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }

    private fun startTimer() {
        stopTimer()
        if (mBinding.tvResend.isEnabled) {
            mBinding.tvResend.isEnabled = false
            mBinding.tvResend.setTextColor(resources.getColor(R.color.grey))
        }

        timer = object : CountDownTimer(31000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(1000),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )
                mBinding.tvTimer.text = time
                if (time == getString(R.string._00_01)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        mBinding.let {
                            it.tvTimer.text = getString(R.string._00_00)
                            it.tvResend.isEnabled = true
                            it.tvResend.setTextColor(resources.getColor(R.color.black))
                        }
                    }, 1000)
                }
            }

            override fun onFinish() {
            }
        }

        timer!!.start()
    }

    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer!!.onFinish()
            mBinding.let {
                it.tvResend.isEnabled = true
                it.tvResend.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
        keyboardHeightProvider.close()
//        (requireActivity() as AccountHandlerActivity).hideKeyboard()
    }

    override fun onKeyboardHeightChanged(height: Int, orientation: Int) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.llContinue.visibility = View.VISIBLE
            if (height > 1) {
                val measuredHeight =
                    (mBinding.root.height - (height + mBinding.llContinue.height + 70)).toFloat()
                if (measuredHeight < 700)
                    mBinding.llContinue.y = measuredHeight + 70
                else
                    mBinding.llContinue.y = measuredHeight
                mBinding.llContinue.requestLayout()
            } else {
                mBinding.llContinue.y = mBinding.guideline3.y

                mBinding.llContinue.requestLayout()
            }
        }
    }


    private fun setupSmsListenerRequest() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null).addOnSuccessListener {
//            Toast.makeText(requireContext(), "On Success", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
//            Toast.makeText(requireContext(), "On OnFailure", Toast.LENGTH_LONG).show()
        }
    }

    private fun registerOTPReceiver() {
        smsBroadcastReceiver = SmsReceiver()
        smsBroadcastReceiver.smsBroadcastReceiverListener =
            object : SmsReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    commonCallbacks?.hideKeyboard()
                    requireActivity().startActivityForResult(intent, REQ_USER_CONSENT)
                }

                override fun onFailure() {
                    println("SMS RECEIVE FAILED")
                }

            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        (requireActivity() as AccountHandlerActivity).registerReceiver(
            smsBroadcastReceiver, intentFilter
        )
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            commonCallbacks?.showKeyboard()
            if (resultCode == RESULT_OK) {
                val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                message?.let { getOtpFromMessage(it) }
            }
        }

    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
        val pattern: Pattern = Pattern.compile("(\\d{4})")
        val matcher: Matcher = pattern.matcher(message)
        if (matcher.find()) {
            mBinding.pvOTP.setText(matcher.group(0))
        }
    }

    private fun unregisterOTPReceiver() {
        try {
            requireActivity()?.unregisterReceiver(smsBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
