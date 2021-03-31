package com.demo.dataSources.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.UpdateNotificationStatusRequest
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService
import retrofit2.HttpException

class NotificationDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : NotificationDataSource {
    override suspend fun getNotifications(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getNotifications(
                params.tab,
                params.notificationLimit,
                params.notificationOffset,
                params.suggestedLimit,
                params.suggestedOffset
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getGetNotificationCount(): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getTotalNoOfNotification()
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun readNotification(id: String): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.readNotification(id)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun updateNotificationStatus(status: Boolean): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updateNotificationStatus(
                UpdateNotificationStatusRequest(notificationStatus = status)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followUser(id: String): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followUser(User(userId = id))
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followUser(payload: User): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followUser(
                payload
            )
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            }
            res.postValue(baseResponse)
        }
        return res

    }
}