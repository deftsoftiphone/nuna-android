package com.demo.repository.home

import androidx.lifecycle.LiveData
import com.demo.dataSources.discover.DiscoverDataSource
import com.demo.dataSources.discover.NotificationDataSource
import com.demo.dataSources.profile.ProfileDataSource
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
    private val discoverDataSource: DiscoverDataSource,
    private val profileDataSource: ProfileDataSource,
    private val notificationDataSource: NotificationDataSource
) : HomeRepository {

    override suspend fun getDiscoverFollowing(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext discoverDataSource.getDiscoverFollowing(queryParams)
        }
    }

    override suspend fun getDiscoverPopular(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext discoverDataSource.getDiscoverPopular(queryParams)
        }
    }

    override suspend fun getOtherUserDetails(queryParams: QueryParams): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.getOtherUserDetails(queryParams)
        }
    }

    override suspend fun getOtherUserPosts(queryParams: QueryParams): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.getOtherUserPosts(queryParams)
        }
    }

    override suspend fun getUserDetails(): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.getUserDetails()
        }
    }

    override suspend fun getUserPosts(): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.getUserPosts()
        }
    }

    override suspend fun checkUserNameAvailability(payload: User): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.checkUserAvailability(payload)
        }
    }

    override suspend fun updateUserDetails(payload: PutUserProfile): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.updateUserDetails(payload)
        }
    }

    override suspend fun updateUserName(payload: PutUserProfile): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.updateUserName(payload)
        }
    }



    override fun getProfileData(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = profileDataSource.getProfileData(queryParams)
            onResult(response.success!!, response)
        }    }

    override suspend fun getNotifications(queryParams: QueryParams): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext notificationDataSource.getNotifications(queryParams)
        }
    }

    override suspend fun followUser(payload: User): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext notificationDataSource.followUser(payload)
        }
    }

    override suspend fun updateUserProfile(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext profileDataSource.updateUserProfilePic(requestSavePost)
        }
    }
}