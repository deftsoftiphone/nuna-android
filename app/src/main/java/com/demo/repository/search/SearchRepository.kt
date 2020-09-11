package com.demo.repository.hashtag

import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface SearchRepository {
    fun getSearchResult(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun followUser(
        payload: User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )
}