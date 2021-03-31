package com.demo.muebert.repository

import com.demo.muebert.modal.BaseRequest
import com.demo.muebert.modal.BaseResponse
import com.demo.muebert.network.dataSource.MuebertDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MuebertRepositoryImpl(
    private val muebertDataSource: MuebertDataSource
) : MuebertRepository {
    override fun getAccess(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = muebertDataSource.getAccess(request)
            onResult(response.status == 1, response)
        }
    }

    override fun getMusic(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = muebertDataSource.getMusic(request)
            onResult(response.status == 1, response)
        }
    }

    override fun recordTrack(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = muebertDataSource.recordTrack(request)
            onResult(response.status == 1, response)
        }
    }

    override suspend fun getTrack(
        request: BaseRequest,
        onResult: (isSuccess: Boolean, response: BaseResponse) -> Unit
    )  = withContext(Dispatchers.IO) {
        val response = muebertDataSource.getTrack(request)
        onResult(response.status == 1, response)
    }
}