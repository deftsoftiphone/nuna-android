package com.demo.username_set

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestSaveUserProfile
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetProfile
import com.demo.model.response.ResponseSaveUserProfile
import com.demo.webservice.ApiRegister

class UsernameSetProfileViewModel(controller: AsyncViewController) : BaseViewModel(controller) {


    val responseGetProfile = MutableLiveData<MasterResponse<ResponseGetProfile>>()
    val responseSaveUserProfile = MutableLiveData<MasterResponse<ResponseSaveUserProfile>>()


    val requestSaveUserProfile = ObservableField<RequestSaveUserProfile>()


    fun callSaveUserProfileApi() {
        baseRepo.restClient.callApi(ApiRegister.SAVEUSERPROGILE, requestSaveUserProfile.get(), responseSaveUserProfile)
    }



}