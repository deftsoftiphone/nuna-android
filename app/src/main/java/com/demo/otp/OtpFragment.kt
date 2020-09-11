package com.demo.otp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentOtpBinding
import com.demo.interfaces.OTPReceiveInterface
import com.demo.model.request.RequestUserRegister
import com.demo.model.response.baseResponse.User
import com.demo.providers.socketio.SocketIO
import com.demo.receivers.SMSListener
import com.demo.receivers.SmsBroadcastReceiverListener
import com.demo.util.*
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


class OtpFragment : BaseFragment(), OTPReceiveInterface, KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: OtpViewModelFactory by instance()
    private val socketIO: SocketIO by instance()
    private val mySMSBroadcastReceiver = SMSListener()
    private lateinit var smsListener: SmsBroadcastReceiverListener
    private var keyboardListener: Unregistrar? = null
    private var onConnectError: Emitter.Listener? = null

    //    private val timer: GlobalTimer by instance()
    private var timer: CountDownTimer? = null

    override fun onOtpReceived(otp: String) {
        //tvOtp.text = "OTP is : $otp"
        mBinding.pvOTP.setText(otp)
    }

    override fun onOtpTimeout() {
        //tvOtp.text = "Time out, please resend"
    }

    lateinit var mBinding: FragmentOtpBinding
    lateinit var mViewModel: OtpViewModel


    override fun onResume() {
        super.onResume()
        mBinding.pvOTP.requestFocus();
        val inputMethodManager =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(mBinding.pvOTP, 0)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        keyboardShowListener()
        socketIO.connect()
    }

    override fun onStop() {
        super.onStop()
        socketIO.disconnect()
    }

    private fun keyboardShowListener() {
        keyboardListener = KeyboardVisibilityEvent.registerEventListener(
            activity,
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    if (isOpen) removeConstraint() else addConstraint()
                    requireActivity().window.setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    )
                }
            })
    }

    override fun onPause() {
        super.onPause()
        keyboardListener!!.unregister()
    }

    private fun removeConstraint() {
        val set = ConstraintSet()
        set.clone(mBinding.clParent)
        TransitionManager.beginDelayedTransition(mBinding.clParent)
        set.clear(R.id.llContinue, ConstraintSet.TOP)
        set.applyTo(mBinding.clParent)
    }

    private fun addConstraint() {
        val set = ConstraintSet()
        set.clone(mBinding.clParent)
        TransitionManager.beginDelayedTransition(mBinding.clParent)
        set.connect(R.id.llContinue, ConstraintSet.TOP, R.id.tvResend, ConstraintSet.BOTTOM)
        set.applyTo(mBinding.clParent)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ActivityCompat.requestPermissions(
            (requireActivity() as AccountHandlerActivity),
            arrayOf<String>(android.Manifest.permission.RECEIVE_SMS),
            100
        )
    }

    override fun onDestroy() {
        SMSListener.unbindListener()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set otp callback from broadcast receiver
//        mySMSBroadcastReceiver.setOnOtpListeners(this)

        //Registering Receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        activity?.applicationContext?.registerReceiver(mySMSBroadcastReceiver, intentFilter)
        setupViewModel()
        mViewModel.parseBundle(arguments)
        setupSMSListener()
    }

    private fun setupSMSListener() {
        SMSListener.bindListener(object : SmsBroadcastReceiverListener {
            override fun onSuccess(message: String?) {
                message?.let {
                    mBinding.pvOTP.setText(mViewModel.getOTP(message))
                }
            }

            override fun onFailure() {

            }

        })
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
        mBinding.lifecycleOwner = viewLifecycleOwner

        Util.updateStatusBarColor(true, "#FFFFFF", activity as FragmentActivity)
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

        setupSocketListeners()
        return mBinding.root

    }


    private fun setupSocketListeners() {
        socketIO.listenOn(Socket.EVENT_CONNECT,
            Emitter.Listener { args ->
                Log.d("EVENT CONNECTED", socketIO.isConnected().toString())
            })

        socketIO.listenOn(Socket.EVENT_CONNECT_ERROR,
            Emitter.Listener { args -> Log.d("EVENT CONNECT ERROR", args[0].toString()) })


        socketIO.listenOn(getString(R.string.socket_listen_user_login), Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                Log.d("LOGIN SUCCESS", args[0].toString())
            }
        })
    }


    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(OtpViewModel::class.java)
    }

    private fun verifyOTP() = launch {
        commonCallbacks?.showProgressDialog()
        mViewModel.verifyOTP().observe(viewLifecycleOwner, Observer {
            commonCallbacks?.hideProgressDialog()
            if (it.error == null && it.success!!) {
                if (it.user?.isDeactivated!!) {
                } else {
                    stopTimer()
                    sendLoginStatusToSocket(it.user!!)
                    Prefs.init().currentUser = it.user
                    Prefs.init().currentToken = it.token!!
                    startActivity(Intent(requireContext(), DashboardActivity::class.java))
                    (requireActivity() as AccountHandlerActivity).finish()
                }
            } else {
                handleAPIError(it.error!!.message.toString())
                mBinding.pvOTP.setText("")
            }
        })
    }

//    user-logs-in => when user logsIn <= 9468105119login-success

    private fun sendLoginStatusToSocket(user: User) {
        if (socketIO.isConnected()) {
            socketIO.emitMessage(
                getString(R.string.socket_user_login),
                toJsonObject("userId" to "${user.id}", "success" to "true")
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
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    inner class ClickHandler {
        fun backPress() {
            findNavController().navigateUp()
        }

        fun onClickOTPSend() {
            commonCallbacks?.hideKeyboard()
            mViewModel.requestOtp.get()!!.apply {
                mobileNo = mViewModel.requestRegister.get()?.mobileNo
                languageId = Prefs.init().selectedLang._id
                countryCode = mViewModel.requestRegister.get()?.countryCode
            }

            if (mViewModel.isOTPValid()) {
                verifyOTP()
            }
        }

        fun onClickResentOTP() {
            mBinding.pvOTP.setText("")
            //mBinding.pinOtp.clearFocus()
            commonCallbacks?.hideKeyboard()
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
                    Handler().postDelayed({
                        mBinding?.let {
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
            mBinding?.let {
                it.tvResend.isEnabled = true
                it.tvResend.setTextColor(resources.getColor(R.color.black))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopTimer()
    }
}
