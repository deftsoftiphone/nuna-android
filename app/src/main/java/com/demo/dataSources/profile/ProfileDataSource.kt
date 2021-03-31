package com.demo.dataSources.profile

import androidx.lifecycle.LiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface ProfileDataSource {

    suspend fun getUserDetails(): LiveData<BaseResponse>

    suspend fun getOtherUserDetails(queryParams: QueryParams): LiveData<BaseResponse>

    suspend fun getOtherUserPosts(
        queryParams: QueryParams

    ): LiveData<BaseResponse>

    suspend fun getUserPosts(
        params: QueryParams
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

    suspend fun removePost(params: QueryParams): BaseResponse

    suspend fun getUserBoard(params: QueryParams): BaseResponse
}