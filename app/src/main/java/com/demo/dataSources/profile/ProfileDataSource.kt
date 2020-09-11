package com.demo.dataSources.profile

import android.service.voice.AlwaysOnHotwordDetector
import androidx.lifecycle.LiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.*

interface ProfileDataSource {

    suspend fun getUserDetails(): LiveData<BaseResponse>

    suspend fun getOtherUserDetails(queryParams: QueryParams): LiveData<BaseResponse>

    suspend fun getOtherUserPosts(
        queryParams: QueryParams

    ): LiveData<BaseResponse>
    suspend fun getUserPosts(

    ): LiveData<BaseResponse>

    suspend fun checkUserAvailability(
        payload: User
    ): LiveData<BaseResponse>

    suspend fun updateUserDetails(
        payload: PutUserProfile
    ): LiveData<BaseResponse>

    suspend fun updateUserName(payload: PutUserProfile): LiveData<BaseResponse>

    suspend fun updateUserProfilePic(requestSavePost: RequestSavePost): LiveData<BaseResponse>


    suspend fun getProfileData(params: QueryParams): BaseResponse

}