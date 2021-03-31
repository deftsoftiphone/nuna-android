package com.demo.dataSources.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.dataSources.search.SearchDataSource
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService
import retrofit2.HttpException

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
                params.searchText,
                resources.getString(R.string.forDashboard)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followUser(request:User): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followUser(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

}