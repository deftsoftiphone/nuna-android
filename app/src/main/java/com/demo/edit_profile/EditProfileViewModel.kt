package com.demo.edit_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.User
import com.demo.model.response.baseResponse.UserProfile
import com.demo.repository.home.HomeRepository

class EditProfileViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    var dataToShare=MutableLiveData<UserProfile>()

    init{
        dataToShare.value= UserProfile()
    }

    suspend fun checkUserAvailablity(paylod: User): LiveData<BaseResponse> {
        return homeRepository.checkUserNameAvailability(paylod)
    }

    suspend fun updateUserDetails(paylod: PutUserProfile): LiveData<BaseResponse> {
        return homeRepository.updateUserDetails(paylod)
    }
    suspend fun updateUserName(paylod: PutUserProfile): LiveData<BaseResponse> {
        return homeRepository.updateUserName(paylod)
    }
    suspend fun updateProfilePic(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        return homeRepository.updateUserProfile(requestSavePost)
    }
}