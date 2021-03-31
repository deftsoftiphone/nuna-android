package com.demo.dataSources.hashtag

import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService

class HashTagDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : HashTagDataSource {
    override suspend fun getCategories(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getDashboardCategories(params.offset, params.limit)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getHashTagOverview(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getHashTagOverview(
                params.postOffset,
                params.postLimit,
                params.hashTagOffset,
                params.hashTagLimit,
                params.categoryId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getAllPostOfAHashTag(id: String, params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getAllPostOfAHashTag(
                id,
                params.limit,
                params.offset,
                params.categoryId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followHashTag(payload: FollowHashTagRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followHashTag(payload)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getHashTagBanner(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getHashTagBanner(params.offset, params.limit)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }
}