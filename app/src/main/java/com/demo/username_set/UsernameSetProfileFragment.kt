package com.demo.username_set

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.demo.R
import com.demo.activity.AccountSetupActivity
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentUsernameSetBinding
import com.demo.model.request.RequestSaveUserProfile
import com.demo.model.response.ResponseLogin
import com.demo.util.Prefs


class UsernameSetProfileFragment : BaseFragment() {

    lateinit var mBinding: FragmentUsernameSetBinding
    lateinit var mViewModel: UsernameSetProfileViewModel
    val mClickHandler by lazy { ClickHandler() }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setupObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUsernameSetBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@UsernameSetProfileFragment
            viewModel = mViewModel
            clickHandler = ClickHandler()
        }


        commonCallbacks?.setupToolBar(
            mBinding.toolbarLayout,
            true,
            getString(R.string.create_usrname)
        )

        return mBinding.root

    }



    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(UsernameSetProfileViewModel::class.java)

    }

    private fun setupObserver() {

        mViewModel.responseSaveUserProfile.observe(this, Observer {
            if (it.data != null) {


              var responseLogin : ResponseLogin
                responseLogin= Prefs.init().loginData!!

                responseLogin.userName= it.data!!.userName

                Prefs.init().loginData=responseLogin

                showAlertDialog(getString(R.string.usrnam_updated), DialogInterface.OnClickListener{_,_ ->
                    startActivity(Intent(requireContext(), AccountSetupActivity::class.java))
                    activity?.finish()

                })
            }
        })


    }

   /* override fun onApiRequestFailed(apiUrl: String, errCode: Int, errorMessage: String): Boolean {
        showAlertDialog(getString(R.string.usrnam_updated), DialogInterface.OnClickListener{_,_ ->
            startActivity(Intent(requireContext(), AccountSetupActivity::class.java))
            activity?.finish()

        })
        return super.onApiRequestFailed(apiUrl, errCode, errorMessage)
    }*/

    inner class ClickHandler {


    /*    "phoneNumber": "9414993834",
        "countryCallingCode": "+91",
        "latitude": 0.0,
        "longitude": 0.0,
        "location": "",
        "userId": 53,
        "email": "",
        "gender":"MALE",
        "userName":"pyyuytt"
*/
        fun onClickSave() {
        mViewModel.requestSaveUserProfile.set(RequestSaveUserProfile())


        if (!mBinding.edtUsername.text.isNullOrBlank()){
            mViewModel.requestSaveUserProfile.get()?.apply {

                phoneNumber= Prefs.init().loginData?.phoneNumber
                countryCallingCode= Prefs.init().loginData?.countryCallingCode
                latitude=0.0
                latitude=0.0
                userId=Prefs.init().loginData?.userId
                email=""
                firstName=Prefs.init().loginData?.firstName
                lastName=Prefs.init().loginData?.lastName
                bio=Prefs.init().loginData?.bio
                profileImage=Prefs.init().loginData?.profileImage

                gender="FEMALE"
                userName=mBinding.edtUsername.text.trim().toString()
            }
            mViewModel.callSaveUserProfileApi()
        }else{
            showAlertDialog("Please enter a Username", DialogInterface.OnClickListener{ _, _ ->


            })
        }

        }




    }






}
