package com.demo.forgot_password


import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.demo.base.AsyncViewController
import com.demo.base.BaseFragment
import com.demo.base.MyViewModelProvider
import com.demo.databinding.FragmentForgotPasswordBinding
import com.demo.model.request.RequestForgotPassword



class ForgotPasswordFragment : BaseFragment() {

    lateinit var mViewModel: ForgotPasswordViewModel
    lateinit var mBinding: FragmentForgotPasswordBinding

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
        mBinding = FragmentForgotPasswordBinding.inflate(inflater, container, false).apply {
            clickHandler = ClickHandler()
            viewModel = mViewModel
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonCallbacks?.setupToolBar(mBinding.toolbarLayout, true, "")
        commonCallbacks?.setupActionBarWithNavController(mBinding.toolbarLayout.toolbar)
    }

    private fun setupViewModel() {
        mViewModel =
            ViewModelProviders.of(this, MyViewModelProvider(commonCallbacks as AsyncViewController))
                .get(
                    ForgotPasswordViewModel::class.java
                )
        mViewModel.requestForgotPassword.set(RequestForgotPassword())
    }

    private fun setupObserver() {
        mViewModel.responseForgotPassword.observe(this, Observer {
            if (it.responseCode == 200) {
                commonCallbacks?.showAlertDialog(
                    it.successMsg,
                    DialogInterface.OnClickListener { _, _ ->
                        findNavController().popBackStack()
                    })
            }
        })
    }

    inner class ClickHandler {

        fun onClickSubmit() {
            commonCallbacks?.hideKeyboard()
            if (mViewModel.validateInput()) {
                mViewModel.callForgotPasswordApi()
            }
        }
    }
}
