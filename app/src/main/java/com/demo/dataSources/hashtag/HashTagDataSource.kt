package com.demo.dataSources.hashtag

import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface HashTagDataSource {
    suspend fun getCategories(params: QueryParams): BaseResponse
    suspend fun getHashTagOverview(params: QueryParams): BaseResponse
    suspend fun getAllPostOfAHashTag(id: String, params: QueryParams): BaseResponse
    suspend fun followHashTag(payload: FollowHashTagRequest): BaseResponse
}