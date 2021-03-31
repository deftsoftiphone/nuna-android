package com.demo.dataSources.viewPost

import com.demo.model.response.baseResponse.*

interface ViewPostDataSource {
    suspend fun likePost(request: LikePostRequest): BaseResponse
    suspend fun getPostComments(params: QueryParams): BaseResponse
    suspend fun likeComment(request: LikeCommentRequest): BaseResponse
    suspend fun addComment(request: AddCommentRequest): BaseResponse
    suspend fun reportPost(request: ReportPostRequest): BaseResponse
    suspend fun sharePost(request: SharePostRequest): BaseResponse
    suspend fun savePost(request: SavePostRequest):BaseResponse
    suspend fun followUser(request: User):BaseResponse
    suspend fun getPostDetail(request: PostDetailRequest):BaseResponse
    suspend fun updatePostViewDuration(request: UpdatePostDurationRequest):BaseResponse
}