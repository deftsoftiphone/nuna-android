package com.demo.muebert.repository

import com.demo.muebert.modal.BaseRequest
import com.demo.muebert.modal.BaseResponse

interface MuebertRepository {
    fun getAccess(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    )

    fun getMusic(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    )

     fun recordTrack(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    )

    suspend fun getTrack(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    )
}