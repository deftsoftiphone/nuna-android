package com.demo.dataSources.searchNotificationDataSourceImpl

import com.demo.dataSources.search.SearchDataSource
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService

class SearchDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : SearchDataSource {
    override suspend fun getSearchResults(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.search(
                params.tab!!,
                params.offset,
                params.limit,
                params.startsWith,
                params.searchText
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followUser(payload: User): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followUser(payload)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }
}