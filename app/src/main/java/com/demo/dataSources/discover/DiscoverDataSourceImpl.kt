package com.demo.dataSources.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService
import retrofit2.HttpException

class DiscoverDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : DiscoverDataSource {

    override suspend fun getDiscoverFollowing(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getDiscoverFollowing(
                queryParams.offset,
                queryParams.limit,
                queryParams.authDeviceToken
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getDiscoverPopular(queryParams: QueryParams): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            if (!queryParams.categoryId.isNullOrEmpty() && queryParams.categoryId != null) {
                baseResponse = apiService.getDiscoverPopularCateggories(
                    queryParams.offset,
                    queryParams.limit,
                    queryParams.categoryId,
                    queryParams.useRecombee,
                    queryParams.authDeviceToken
                ).await()
            } else {
                baseResponse =
                    apiService.getDiscoverPopular(
                        queryParams.offset,
                        queryParams.limit,
                        queryParams.useRecombee,
                        queryParams.authDeviceToken
                    ).await()
            }
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun getCategories(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getDashboardCategories(params.offset, params.limit)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
        }
        return baseResponse
    }
}