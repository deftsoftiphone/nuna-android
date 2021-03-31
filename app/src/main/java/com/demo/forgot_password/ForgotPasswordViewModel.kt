package com.demo.forgot_password
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestForgotPassword
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLogin
import com.demo.util.Validator
import com.demo.webservice.ApiRegister


class ForgotPasswordViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    val requestForgotPassword = ObservableField<RequestForgotPassword>()
    val responseForgotPassword = MutableLiveData<MasterResponse<ResponseLogin>>()

    val errEmail = ObservableField<String>()

    fun validateInput(): Boolean {

        val data = requestForgotPassword.get() ?: return false

        if (!Validator.isEmailValid(data.email, errEmail)){
            return false
        }

        return true
    }

    fun callForgotPasswordApi() {
        baseRepo.restClient.callApi(ApiRegister.FORGOT_PASSWORD, requestForgotPassword.get(), responseForgotPassword)
    }
}