package com.demo.dataSources.discover

import androidx.lifecycle.LiveData
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface DiscoverDataSource {
    suspend fun getDiscoverFollowing(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getDiscoverPopular(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getCategories(params: QueryParams): BaseResponse
}