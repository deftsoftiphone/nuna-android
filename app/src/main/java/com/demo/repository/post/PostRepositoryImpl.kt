package com.demo.repository.post

import androidx.lifecycle.LiveData
import com.demo.dataSources.post.PostDataSource
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepositoryImpl(private val postDataSource: PostDataSource) : PostRepository {
    override suspend fun getCategories(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.getCategories(queryParams)
        }
    }

    override suspend fun getHashTags(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.getHashTags(queryParams)
        }
    }

    override suspend fun createPost(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext postDataSource.createPost(requestSavePost)
        }
    }
}