package com.demo.changepassword

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestChangePassword
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseChangePassword
import com.demo.util.Validator
import com.demo.webservice.ApiRegister


class ChangePasswordViewModel(controller : AsyncViewController) : BaseViewModel(controller){

    val requestChangePassword = ObservableField<RequestChangePassword>(RequestChangePassword())
    val responseChangePassword = MutableLiveData<MasterResponse<ResponseChangePassword>>()
    val errOldPassword = ObservableField<String>()
    val errNewPassword = ObservableField<String>()
    val errConfirmPassword = ObservableField<String>()

    private fun callChangePasswordApi(){
        baseRepo.restClient.callApi(ApiRegister.CHANGE_PASSWORD, requestChangePassword.get(), responseChangePassword)
    }

    fun requestChangePassword() {
        if (Validator.validateChangePasswordForm(requestChangePassword.get()!!, errOldPassword, errNewPassword, errConfirmPassword)){
            controller?.hideKeyboard()
            callChangePasswordApi()
        }
    }
}