package com.demo.repository.viewPost

import com.demo.model.response.baseResponse.*

interface ViewPostRepository {

    suspend fun likePost(
        request: LikePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun getPostComments(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun likeComment(
        request: LikeCommentRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun addComment(
        request: AddCommentRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun reportPost(
        request: ReportPostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun sharePost(
        request: SharePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun savePost(
        request: SavePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun followUser(
        user: User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )

    suspend fun getPostDetail(
        request: PostDetailRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )


    suspend fun updatePostViewDuration(
        request: UpdatePostDurationRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )
}