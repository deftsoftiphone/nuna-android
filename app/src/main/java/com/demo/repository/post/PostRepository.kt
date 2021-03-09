package com.demo.repository.post

import androidx.lifecycle.LiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.AddPostRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface PostRepository {
    suspend fun getCategories(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun getHashTags(
        queryParams: QueryParams
    ): LiveData<BaseResponse>

    suspend fun getHashTag(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun createPost(
        requestSavePost: RequestSavePost
    ): LiveData<BaseResponse>

    suspend fun createPostV2(
        request: AddPostRequest
    ): LiveData<BaseResponse>


}