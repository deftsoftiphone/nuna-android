package com.demo.muebert.network.dataSource

import com.demo.muebert.modal.BaseRequest
import com.demo.muebert.modal.BaseResponse

interface MuebertDataSource {
    suspend fun getAccess(request: BaseRequest):BaseResponse
    suspend fun getMusic(request: BaseRequest): BaseResponse
    suspend fun recordTrack(request: BaseRequest): BaseResponse
    suspend fun getTrack(request: BaseRequest): BaseResponse
}