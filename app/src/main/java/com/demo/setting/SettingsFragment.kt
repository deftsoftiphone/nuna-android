package com.demo.setting

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.demo.MainActivity
import com.demo.R
import com.demo.activity.AccountHandlerActivity
import com.demo.activity.DashboardActivity
import com.demo.base.BaseFragment
import com.demo.databinding.FragmentSettingsBinding
import com.demo.providers.socketio.SocketIO
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.util.Validator
import com.demo.util.toJsonObject
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


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


        //commonCallbacks?.setupToolBar(mBinding.toolbarLayout, false, "")


        Log.e("CurrentLanguage", "${Locale.getDefault().getDisplayLanguage()}")

        mBinding.switchButton.setChecked(Prefs.init().notificationAll);


        mBinding.switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // The switch is enabled/checked
                Prefs.init().notificationAll = isChecked
            } else {
                // The switch is disabled
                Prefs.init().notificationAll = isChecked
                // Set the app background color to light gray
            }
        }


        return mBinding.root

    }

    private fun setupSocketListeners() {
        socketIO.listenOn(
            Socket.EVENT_CONNECT,
            Emitter.Listener { args ->
                Log.e("EVENT CONNECTED", socketIO.isConnected().toString())
            })

        socketIO.listenOn(
            Socket.EVENT_CONNECT_ERROR,
            Emitter.Listener { args -> Log.e("EVENT CONNECT ERROR", args[0].toString()) })

        socketIO.listenOn("logout-success", Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                Log.e("LOGOUT SUCCESS", args[0].toString())
            }
        })
    }

    private fun emitLogoutToServer(userId: String) {
        println(
            "socket Request = ${toJsonObject(
                "userId" to userId,
                "success" to "true"
            )}"
        )
        if (socketIO.isConnected()) {
            socketIO.emitMessage(
                "user-logs-out",
                toJsonObject("userId" to userId, "success" to "true")
            )
        }
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(SettingsViewModel::class.java)

    }

    private fun setupObserver() {
        mViewModel.apply {
            responseLogin.observe(viewLifecycleOwner, Observer {
                if (it.data != null) {
                    Prefs.init().loginData = it.data
                    Prefs.init().userCreds = mViewModel.requestLogin.get()
                    Prefs.init().rememberMe = mViewModel.requestLogin.get()?.flagRememberMe ?: false
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                }
            })

            logout.observe(viewLifecycleOwner, Observer {
                if (it) {
                    emitLogoutToServer(Prefs.init().profileData!!.id!!)
                    Prefs.init().loginData = null
                    Prefs.init().profileData = null
                    Prefs.init().currentUser = null
                    Prefs.init().currentToken = ""
                    startActivity(Intent(requireContext(), AccountHandlerActivity::class.java))
                    activity?.finish()
                }
            })

            showLoading.observe(viewLifecycleOwner, Observer {
                (requireActivity() as DashboardActivity).apply {
                    if (it == true) showProgressDialog() else hideProgressDialog()
                }
            })


            toastMessage.observe(viewLifecycleOwner, Observer {
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
            onFragBack()
        }
    }

    fun showLogoutDialog() {
        commonCallbacks!!.showAlertDialog(
            "",
            getString(R.string.msg_app_logout_confirmation),
            getString(R.string.yes),
            getString(R.string.no),
            DialogInterface.OnClickListener { _, which ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    // mBaseViewModel.logOut()
                    mViewModel.logoutUser()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        socketIO.connect()
    }

    override fun onStop() {
        super.onStop()
        socketIO.disconnect()
    }
}
