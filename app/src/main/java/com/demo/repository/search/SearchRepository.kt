package com.demo.repository.search

import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User

interface SearchRepository {
    fun getSearchResult(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    fun followUser(
        request:User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )
}