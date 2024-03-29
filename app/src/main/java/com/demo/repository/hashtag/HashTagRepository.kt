package com.demo.repository.hashtag

import androidx.lifecycle.LiveData
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface HashTagRepository {
    suspend fun getCategories(queryParams: QueryParams): LiveData<BaseResponse>
    fun getCategories(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun getHashTagOverview(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun getAllPostOfAHashTag(
        id: String, queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun followHashTag(
        payload: FollowHashTagRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

}