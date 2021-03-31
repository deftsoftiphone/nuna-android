package com.demo.dataSources.notifications

import androidx.lifecycle.LiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface NotificationDataSource {
    /*
        suspend fun getNotifications(
            queryParams: QueryParams
        ): LiveData<BaseResponse>

    */
    suspend fun followUser(
        payload: User
    ): LiveData<BaseResponse>

    suspend fun getNotifications(params: QueryParams): BaseResponse
    suspend fun followUser(id: String): BaseResponse

    suspend fun getGetNotificationCount(): BaseResponse
    suspend fun readNotification(id: String): BaseResponse
    suspend fun updateNotificationStatus(status:Boolean): BaseResponse
}