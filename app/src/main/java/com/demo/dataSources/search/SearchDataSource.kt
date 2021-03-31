package com.demo.dataSources.search

import androidx.lifecycle.LiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface SearchDataSource {
    suspend fun getSearchResults(params: QueryParams): BaseResponse
    suspend fun followUser(request:User): BaseResponse
}