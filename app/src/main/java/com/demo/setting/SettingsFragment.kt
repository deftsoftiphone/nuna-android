package com.demo.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.MainActivity
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentSettingsBinding
import com.demo.providers.socketio.SocketIO
import com.demo.util.*
import com.github.nkzawa.socketio.client.Socket
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SettingsFragment : BaseFragment(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val socketIO: SocketIO by instance()
    private val viewModelFactory: SettingsViewModelFactory by instance()
    lateinit var mBinding: FragmentSettingsBinding
    lateinit var mViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        setupObserver()
        setupSocketListeners()
        mBinding.switchButton.setOnCheckedChangeListener { p0, p1 ->
            if (Util.checkIfHasNetwork()) {
                mViewModel.updateNotificationStatus(p1)
            } else Validator.showMessage(resources.getString(R.string.connectErr))
        }

        mBinding.switchButton.isChecked = Prefs.init().notificationAll
        return mBinding.root
    }


    private fun setupSocketListeners() {
        socketIO.listenOn(
            Socket.EVENT_CONNECT
        ) { args ->
            Log.e("EVENT CONNECTED", socketIO.isConnected().toString())
        }

        socketIO.listenOn(
            Socket.EVENT_CONNECT_ERROR
        ) { args -> Log.e("EVENT CONNECT ERROR", args[0].toString()) }

        socketIO.listenOn("logout-success") { args ->
            if (args.isNotEmpty()) {
                Log.e("LOGOUT SUCCESS", args[0].toString())
            }
        }
    }

    private fun emitLogoutToServer(userId: String) {
        if (socketIO.isConnected()) {
            socketIO.emitMessage(
                "user-logs-out",
                toJsonObject(
                    "userId" to userId,
                    "deviceToken" to requireContext().getUniqueToken(),
                    "success" to "true"
                )
            )
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)
    }

    private fun setupObserver() {
        mViewModel.apply {
            responseLogin.observe(viewLifecycleOwner, {
                if (it.data != null) {
                    Prefs.init().loginData = it.data
                    Prefs.init().userCreds = mViewModel.requestLogin.get()
                    Prefs.init().rememberMe = mViewModel.requestLogin.get()?.flagRememberMe ?: false
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                }
            })

            logout.observe(viewLifecycleOwner, {
                if (it) {
                    emitLogoutToServer(Prefs.init().profileData!!.id!!)
                    Prefs.init().loginData = null
                    Prefs.init().profileData = null
                    Prefs.init().currentUser = null
                    Prefs.init().currentToken = ""
                    requireContext().clearNotification()
                    startActivity(Intent(requireContext(), AccountHandlerActivity::class.java))
                    activity?.finish()
                }
            })

            showLoading.observe(viewLifecycleOwner, {
                (requireActivity() as DashboardActivity).apply {
                    if (it == true) showProgressDialog() else hideProgressDialog()
                }
            })


            toastMessage.observe(viewLifecycleOwner, {
                if (it.isNotEmpty())
                    Validator.showMessage(it)
            })
        }
    }

    inner class ClickHandler {

        fun onClickEditProfile() {
            findNavController().navigate(R.id.EditProfileFragment)
        }

        fun onClickHelpPagesP() {
            navigate(
                R.id.helpFragment,
                ParcelKeys.PK_PAGE to "Privacy_Policy"
            )
        }

        fun onClickHelpPagesC() {
            navigate(
                R.id.helpFragment,
                ParcelKeys.PK_PAGE to "Content_Policy"
            )
        }

        fun onClickHelpPagesA() {
            navigate(
                R.id.helpFragment,
                ParcelKeys.PK_PAGE to "About_us"
            )
        }

        fun onClickHelpPagesT() {
            navigate(
                R.id.helpFragment,
                ParcelKeys.PK_PAGE to "Terms_Conditions"
            )
        }

        fun onClickHelpPagesR() {
            navigate(
                R.id.helpFragment,
                ParcelKeys.PK_PAGE to "Reward"
            )
        }

        fun onClickLogOut() {
            showLogoutDialog()
        }

        fun onClickLang() {
            findNavController().navigate(R.id.LanguageSelectFragment, bundleOf("mode" to "update"))
        }

        fun onBackPressed() {
            findNavController().navigateUp()
        }
    }

    fun showLogoutDialog() {
        DialogUtil.build(requireContext()) {
            dialogType = DialogUtil.DialogType.LOGOUT
            yesNoDialogClickListener = object : DialogUtil.YesNoDialogClickListener {
                override fun onClickYes() {
                    if (isNetworkConnected()!!)
                        mViewModel.logoutUser()
                    else Validator.showMessage(getString(R.string.connectErr))
                }

                override fun onClickNo() {}

            }
        }


//        commonCallbacks!!.showAlertDialog(
//            "",
//            getString(R.string.msg_app_logout_confirmation),
//            getString(R.string.yes),
//            getString(R.string.no),
//            DialogInterface.OnClickListener { _, which ->
//                if (which == DialogInterface.BUTTON_POSITIVE) {
//                    // mBaseViewModel.logOut()
//                    if (isNetworkConnected()!!)
//                        mViewModel.logoutUser()
//                    else Validator.showMessage(getString(R.string.no_internet))
//                }
//            })
    }

    override fun onResume() {
        super.onResume()
        socketIO.connect()
    }

    override fun onStop() {
        super.onStop()
//        socketIO.disconnect()
    }
}
