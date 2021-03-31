package com.demo.repository.viewPost

import com.demo.dataSources.viewPost.ViewPostDataSource
import com.demo.model.response.baseResponse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewPostRepositoryImpl(private val viewPostDataSource: ViewPostDataSource) :
    ViewPostRepository {
    override suspend fun likePost(
        request: LikePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.likePost(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun getPostComments(
        params: QueryParams,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.getPostComments(params)
            onResult(response.success!!, response)
        }
    }

    override suspend fun likeComment(
        request: LikeCommentRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.likeComment(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun addComment(
        request: AddCommentRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.addComment(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun reportPost(
        request: ReportPostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.reportPost(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun sharePost(
        request: SharePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.sharePost(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun savePost(
        request: SavePostRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.savePost(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun followUser(
        user: User,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.followUser(user)
            onResult(response.success!!, response)
        }
    }

    override suspend fun getPostDetail(
        request: PostDetailRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.getPostDetail(request)
            onResult(response.success!!, response)
        }
    }

    override suspend fun updatePostViewDuration(
        request: UpdatePostDurationRequest,
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = viewPostDataSource.updatePostViewDuration(request)
            onResult(response.success!!, response)
        }
    }

}