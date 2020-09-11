package com.demo.edit_profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.R
import com.demo.base.BaseFragment
import com.demo.databinding.EditProfileEntityBinding
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile
import com.demo.util.Prefs
import com.demo.util.Util
import com.demo.util.Validator
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class EditProfileEntityfragment : BaseFragment(), KodeinAware {

    private var value: String = ""
    private var username: String = ""
    lateinit var mBinding: EditProfileEntityBinding
    var user1: UserProfile? = null
    lateinit var mViewModel: EditProfileViewModel
    var type: String = ""
    val mClickHandler by lazy { ClickHandler() }
    override val kodein: Kodein by closestKodein()
    private val viewModelFactory: EditProfileViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
//        setupObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = EditProfileEntityBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@EditProfileEntityfragment
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }
        editTextWatcherListner()
        return mBinding.root
    }


    private fun setupViewModel() {
        mViewModel =
            ViewModelProvider(this, viewModelFactory).get(EditProfileViewModel::class.java)
    }

    private fun editTextWatcherListner() {
        val handler = Handler()
        var runnable = Runnable { }

        //Full Name Edit text
        mBinding.editTextName.addTextChangedListener(object : TextWatcher {
            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (count <= 25) {
                    mBinding.textCount.setText("${s?.length}/25")

                }
            }

        })

        //Full Name Edit text
        mBinding.editTextUsername.addTextChangedListener(object : TextWatcher {
            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {

//                mBinding.editTextUsername.setText(s.toString().toLowerCase());

                if (s?.toString()!!.isEmpty()) {

                    mBinding.iCross.visibility = View.VISIBLE
                    mBinding.iCheck.visibility = View.GONE


                } else if (s.toString().equals(value))
                {
                    mBinding.iCross.visibility = View.GONE
                    mBinding.iCheck.visibility = View.VISIBLE
                }

                else {

                    if (isNetworkConnected()!!) {
                        runnable = Runnable {
                            if (!(username.equals(s.toString()))) {
                                checkUserAvailability(true)
                            }
                        }

                        handler.postDelayed(runnable, 2000)
                    } else {
                        Validator.showMessage(getString(R.string.no_internet))

                    }

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                handler.removeCallbacks(runnable)
                if (count <= 25) {
                    mBinding.textCount.text = "${s?.length}/25"
                }

            }

        })

        mBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count <= 150) {
                    mBinding.textCount.setText("${s?.length}/150")
                }


            }

        })
    }

    inner class ClickHandler {
        fun onBackPressed() {
            onFragBack()
        }

        fun onClickCross() {
            clearData()
        }

        fun onClickDone() {
            if (isNetworkConnected()!!) {
                saveDetails()
            } else {
                Validator.showMessage(getString(R.string.no_internet))
            }


        }
    }

    private fun saveDetails() {

        Log.e("Type",type)
        if (type == getString(R.string.user_name)) {
            if (mBinding.editTextUsername.text.trim().toString().isBlank()) {
                Validator.showMessage(getString(R.string.enter_user_name))
            } else {

                if (mBinding.editTextUsername.text.trim().toString().equals(Prefs.init().profileData?.userName)) {
                    onFragBack()

                } else {
                    updateUserName(true)

                }

            }

        } else if (type == getString(R.string.name)) {
            if (mBinding.editTextName.text.trim().toString().isBlank()) {
                Validator.showMessage(getString(R.string.enter_full_name))
            } else {
                updateUserDetails(true)

            }
        } else {
            updateUserDetails(true)

        }

    }

    private fun clearData() {
        if (type == getString(R.string.bio)) {
            mBinding.editText.setText("")

        } else {
            mBinding.editTextName.setText("")

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        user1 = Prefs.init().profileData
        type = arguments?.getString(getString(R.string.type))!!
        value = arguments?.getString(getString(R.string.value))!!
        setUpProfileData()

    }

    private fun setUpProfileData() {
        when (type) {
            getString(R.string.name) -> {
                mBinding.editTextName.visibility = View.VISIBLE
                mBinding.editTextUsername.visibility = View.GONE
                if(!(value==getString(R.string.full_name))){
                    mBinding.editTextName.setText(value)
                }
                mBinding.editTextName.filters = Util.getFilter(25, true)

            }

            getString(R.string.user_name) -> {
                mBinding.editTextName.visibility = View.GONE
                mBinding.editTextUsername.visibility = View.VISIBLE
                mBinding.tUserNameInstructions.visibility = View.VISIBLE

                if (value == getString(R.string.user_name)) {
                    mBinding.editTextUsername.hint = value

                } else {
                    mBinding.lUserNameNotAvailable.visibility = View.GONE
                    mBinding.iCross.visibility = View.GONE
                    mBinding.iCheck.visibility = View.VISIBLE
                    mBinding.editTextUsername.setText(value)

                }

                mBinding.title.text = getString(R.string.user_name)

            }

            getString(R.string.bio) -> {
                mBinding.title.text = getString(R.string.bio)
                mBinding.iCross.visibility = View.GONE
                mBinding.iCrossBio.visibility = View.VISIBLE

                if (value != getString(R.string.add_your_bio)) {
                    mBinding.editText.setText(value)

                }
                mBinding.editText.visibility = View.VISIBLE
                mBinding.editTextName.visibility = View.GONE
                mBinding.editTextUsername.visibility = View.GONE
                mBinding.textCount.setText("${mBinding?.editText.text.trim().length}/150")
            }

        }

    }


    private fun checkUserAvailability(showProgress: Boolean) = launch {
        val user = User()
        user.userName = mBinding.editTextUsername.text.toString()
        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.checkUserAvailablity(user).observe(viewLifecycleOwner, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null && it.success == true) {
                if (it.data?.isAvailable!!) {
                    mBinding.lUserNameNotAvailable.visibility = View.GONE
                    mBinding.iCross.visibility = View.GONE
                    mBinding.iCheck.visibility = View.VISIBLE
                } else {
                    mBinding.lUserNameNotAvailable.visibility = View.VISIBLE
                    mBinding.iCross.visibility = View.VISIBLE
                    mBinding.iCheck.visibility = View.GONE
                }
            } else {
                mBinding.lUserNameNotAvailable.visibility = View.VISIBLE
                mBinding.iCross.visibility = View.VISIBLE
                mBinding.iCheck.visibility = View.GONE
            }
        })


    }


    private fun updateUserDetails(showProgress: Boolean) = launch {
        val user = PutUserProfile()
        if (type == getString(R.string.name)) {
            user1?.fullName = mBinding.editTextName.text.trim().toString()
            user.fullName = mBinding.editTextName.text.trim().toString()
        } else {
            user1?.bioData = mBinding.editText.text.trim().toString()
            user.bioData = mBinding.editText.text.trim().toString()
        }


        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.updateUserDetails(user).observe(viewLifecycleOwner, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                commonCallbacks?.hideKeyboard()
                Prefs.init().profileData = user1
                onFragBack()

            } else {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

    private fun updateUserName(showProgress: Boolean) = launch {
        val user = PutUserProfile()
        user1?.userName = mBinding.editTextUsername.text.trim().toString()
        user.userName = mBinding.editTextUsername.text.trim().toString()

        if (showProgress) commonCallbacks?.showProgressDialog()
        mViewModel.updateUserName(user).observe(viewLifecycleOwner, Observer {
            if (showProgress) commonCallbacks?.hideProgressDialog()
            if (it.error == null) {
                commonCallbacks?.hideKeyboard()
                Prefs.init().profileData = user1
                onFragBack()

            } else {
                handleAPIError(it.error!!.message.toString())
            }
        })
    }

}