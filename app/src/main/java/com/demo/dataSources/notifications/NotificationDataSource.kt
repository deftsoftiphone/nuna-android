package com.demo.dataSources.discover

import android.service.voice.AlwaysOnHotwordDetector
import androidx.lifecycle.LiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface NotificationDataSource {
    suspend fun getNotifications(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun followUser(
        payload: User
    ): LiveData<BaseResponse>
}