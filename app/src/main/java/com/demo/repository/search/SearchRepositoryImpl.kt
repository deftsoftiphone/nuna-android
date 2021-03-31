package com.demo.repository.search

import com.demo.dataSources.search.SearchDataSource
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchRepositoryImpl(
    private val searchDataSource: SearchDataSource
) : SearchRepository {

    override fun getSearchResult(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = searchDataSource.getSearchResults(queryParams)
            onResult(response.success!!, response)
        }
    }

    override fun followUser(
        request: User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = searchDataSource.followUser(request)
            onResult(response.success!!, response)
        }
    }


}