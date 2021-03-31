package com.demo.repository.home

import androidx.lifecycle.LiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface HomeRepository {

    //Discover
    suspend fun getDiscoverFollowing(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getDiscoverPopular(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    //Profile
    suspend fun getOtherUserDetails(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getOtherUserPosts(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getUserDetails(
    ): LiveData<BaseResponse>

    suspend fun getUserPosts(
        params: QueryParams
    ): LiveData<BaseResponse>

    fun getUserBoard(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun checkUserNameAvailability(
        payload: User
    ): LiveData<BaseResponse>

    suspend fun updateUserDetails(
        payload: PutUserProfile
    ): LiveData<BaseResponse>

    suspend fun updateUserName(
        payload: PutUserProfile
    ): LiveData<BaseResponse>

    fun getProfileData(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun removePost(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    //Notification
    fun getNotifications(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun followUser(
        payload: User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun getNotificationCount(
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun readNotification(
        id: String,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )


    /*   suspend fun getNotifications(
        queryParams: QueryParams
    ): LiveData<BaseResponse>
*/
    suspend fun followUser(
        payload: User
    ): LiveData<BaseResponse>


    suspend fun updateUserProfile(
        requestSavePost: RequestSavePost
    ): LiveData<BaseResponse>

    suspend fun updateNotificationStatus(status:Boolean, onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit)

    suspend fun getCategories(params: QueryParams, onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit)
}