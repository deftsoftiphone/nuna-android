package com.demo.muebert.network.dataSource

import com.demo.muebert.modal.BaseRequest
import com.demo.muebert.modal.BaseResponse
import com.demo.muebert.network.APIService
import com.demo.providers.resources.ResourcesProvider

class MuebertDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : MuebertDataSource {
    override suspend fun getAccess(request: BaseRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getAccess(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getMusic(request: BaseRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getMusic(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun recordTrack(request: BaseRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.recordTrack(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getTrack(request: BaseRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getTrack(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

}