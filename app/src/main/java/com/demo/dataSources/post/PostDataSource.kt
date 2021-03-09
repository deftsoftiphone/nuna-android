package com.demo.dataSources.post

import androidx.lifecycle.LiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.AddPostRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface PostDataSource {

    suspend fun getCategories(params: QueryParams): BaseResponse

    suspend fun getHashTag(
        queryParams: QueryParams
    ): BaseResponse

    suspend fun getHashTags(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun createPost(
        requestSavePost: RequestSavePost
    ): LiveData<BaseResponse>

    suspend fun createPostV2(
        request: AddPostRequest
    ): LiveData<BaseResponse>
}