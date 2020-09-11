package com.demo.dataSources.discover

import android.service.voice.AlwaysOnHotwordDetector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService
import retrofit2.HttpException

class NotificationDataSourceImpl(private val apiService: APIService,
                             private val resources: ResourcesProvider) : NotificationDataSource {
    override suspend fun getNotifications(queryParams: QueryParams): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getNotifications(
                queryParams.tab,
                queryParams.limit,
                queryParams.offset
            )
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e,resources)
            }
            res.postValue(baseResponse)
        }
        return res    }

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
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e,resources)
            }
            res.postValue(baseResponse)
        }
        return res      }


}