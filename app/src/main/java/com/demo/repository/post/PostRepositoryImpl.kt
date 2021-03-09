package com.demo.repository.post

import androidx.lifecycle.LiveData
import com.demo.dataSources.post.PostDataSource
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.AddPostRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepositoryImpl(private val postDataSource: PostDataSource) : PostRepository {

    override suspend fun getCategories(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) = withContext(Dispatchers.IO) {
        val response = postDataSource.getCategories(params)
        onResult(response.success!!, response)
    }

    override suspend fun getHashTags(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.getHashTags(queryParams)
        }
    }

    override suspend fun getHashTag(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )  = withContext(Dispatchers.IO) {
        val response = postDataSource.getHashTag(params)
        onResult(response.success!!, response)
    }

    override suspend fun createPost(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.createPost(requestSavePost)
        }
    }

    override suspend fun createPostV2(request: AddPostRequest): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.createPostV2(request)
        }
    }
}