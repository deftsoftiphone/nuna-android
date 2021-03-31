package com.demo.changepassword

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
import com.demo.databinding.FragmentChangePasswordBinding
import com.demo.model.request.RequestChangePassword



class ChangePasswordFragment : BaseFragment() {

    lateinit var mViewModel: ChangePasswordViewModel
    lateinit var mBinding: FragmentChangePasswordBinding

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
        mBinding = FragmentChangePasswordBinding.inflate(inflater, container, false).apply {
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
                .get(ChangePasswordViewModel::class.java)
        mViewModel.requestChangePassword.set(RequestChangePassword())
    }

    private fun setupObserver() {
        mViewModel.responseChangePassword.observe(this, Observer {
            if (it.data != null) {
                commonCallbacks?.showAlertDialog(
                    it.successMsg
                ) { _, _ ->
                    findNavController().popBackStack()
                }
            }
        })
    }

    inner class ClickHandler {

        fun onClickSubmit() {
            mViewModel.requestChangePassword()
        }
    }

}
