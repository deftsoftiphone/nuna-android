package com.demo.repository.hashtag

import androidx.lifecycle.LiveData
import com.demo.dataSources.hashtag.HashTagDataSource
import com.demo.dataSources.post.PostDataSource
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HashTagRepositoryImpl(
    private val postDataSource: PostDataSource,
    private val hashTagDataSource: HashTagDataSource
) : HashTagRepository {
    override suspend fun getCategories(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.getCategories(queryParams)
        }
    }

    override fun getCategories(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = hashTagDataSource.getCategories(queryParams)
            onResult(response.success!!, response)
        }
    }

    override fun getHashTagOverview(
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = hashTagDataSource.getHashTagOverview(queryParams)
            onResult(response.success!!, response)
        }
    }

    override fun getAllPostOfAHashTag(
        id: String,
        queryParams: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = hashTagDataSource.getAllPostOfAHashTag(id, queryParams)
            onResult(response.success!!, response)
        }
    }

    override fun followHashTag(
        payload: FollowHashTagRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = hashTagDataSource.followHashTag(payload)
            onResult(response.success!!, response)
        }
    }
}