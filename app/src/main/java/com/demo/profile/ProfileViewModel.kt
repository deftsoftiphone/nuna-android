package com.demo.profile

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.Pinboard
import com.demo.model.request.RequestAddPinboard
import com.demo.model.request.RequestGetUserBoards
import com.demo.model.request.RequestGetUserProfile
import com.demo.model.request.RequestSavePost
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetProfile
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.webservice.ApiRegister
import com.demo.webservice.Resource

class ProfileViewModel( private val homeRepository: HomeRepository,val resource: ResourcesProvider) : ViewModel() {


    val responseGetProfile = MutableLiveData<MasterResponse<ResponseGetProfile>>()
    val responseAddPinboard = MutableLiveData<MasterResponse<Pinboard>>()
    val queryParams=QueryParams()


    var userPosts = mutableListOf<Post>()
    var savedPosts = mutableListOf<Post>()






    suspend fun getUserDetails(): LiveData<BaseResponse> {
        return homeRepository.getUserDetails()
    }

    suspend fun getOtherUserDetails(id:String?): LiveData<BaseResponse> {
        queryParams.id=id
        return homeRepository.getOtherUserDetails(queryParams)
    }

    suspend fun getUserPosts(): LiveData<BaseResponse> {
        return homeRepository.getUserPosts()
    }

    suspend fun getOtherUserPosts(id:String?): LiveData<BaseResponse> {
        queryParams.id=id
        return homeRepository.getOtherUserPosts(queryParams)
    }

    suspend fun followUser(user : User): LiveData<BaseResponse> {
        return homeRepository.followUser(user)
    }




}