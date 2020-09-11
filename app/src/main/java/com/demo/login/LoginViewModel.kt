package com.demo.login

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.demo.model.request.RequestUserRegister
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.repository.login.LoginRepository
import com.demo.webservice.Resource
import kotlinx.coroutines.Dispatchers


class LoginViewModel(
//    controller: AsyncViewController,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val errPhoneNumber = ObservableField<String>("")
    val requestRegister = ObservableField<RequestUserRegister>()

    init {
        requestRegister.set(RequestUserRegister(countryCode = "+91"))

    }

    suspend fun login(): LiveData<BaseResponse> {
        return loginRepository.login(requestRegister.get()!!)
    }
}


