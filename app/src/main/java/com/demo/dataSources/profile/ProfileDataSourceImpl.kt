package com.demo.dataSources.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PutUserProfile
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.NoInternetException
import com.demo.util.Util.Companion.checkIfHasNetwork
import com.demo.webservice.APIService
import retrofit2.HttpException

class ProfileDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : ProfileDataSource {

    override suspend fun getUserDetails(): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getUserDetails().await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getOtherUserDetails(queryParams: QueryParams): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getOtherUserDetails(
                queryParams.id
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getOtherUserPosts(queryParams: QueryParams): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getOtherUserPosts(
                queryParams.id,
                queryParams.offset,
                queryParams.limit
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getUserPosts(params: QueryParams): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getUserPosts(
                params.offset, params.limit
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun checkUserAvailability(payload: User): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.checkUserNameAvailability(payload)
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun updateUserDetails(payload: PutUserProfile): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updateUserDetails(
                payload
            )
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun updateUserName(payload: PutUserProfile): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updateUserName(
                payload
            )
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun updateUserProfilePic(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updateProfilePicture(
                requestSavePost.profileImage
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error =
                    APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getProfileData(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getProfileData(
                params.tab!!,
                params.offset,
                params.limit,
                params.userId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
        }
        return baseResponse
    }

    override suspend fun removePost(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            if (!checkIfHasNetwork())
                throw NoInternetException()
            baseResponse = apiService.removePost(params.postId!!)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            }
        }
        return baseResponse
    }

    override suspend fun getUserBoard(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            if (!checkIfHasNetwork())
                throw NoInternetException()
            baseResponse = apiService.getUserBoard(
                params.limit,
                params.offset
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }
}