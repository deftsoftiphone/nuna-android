package com.demo.edit_profile

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestAddUserCategory
import com.demo.model.request.RequestGetUserProfile
import com.demo.model.request.RequestSaveUserProfile
import com.demo.model.response.*
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile
import com.demo.repository.home.HomeRepository
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.util.Validator
import com.demo.webservice.ApiRegister
import com.google.gson.Gson

class EditProfileEntityViewModel(val homeRepository: HomeRepository) : ViewModel() {

    suspend fun checkUserAvailablity(paylod:User):LiveData<BaseResponse>{
        return homeRepository.checkUserNameAvailability(paylod)
    }


    suspend fun updateUserDetails(paylod:PutUserProfile):LiveData<BaseResponse>{
        return homeRepository.updateUserDetails(paylod)
    }



}