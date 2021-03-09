package com.demo.dataSources.viewPost

import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.NoInternetException
import com.demo.util.Util.Companion.checkIfHasNetwork
import com.demo.webservice.APIService
import okhttp3.MultipartBody
import retrofit2.HttpException

class ViewPostDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : ViewPostDataSource {
    override suspend fun likePost(request: LikePostRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.likePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getPostComments(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getPostComments(params.postId, params.limit, params.offset)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun likeComment(request: LikeCommentRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.likeComment(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun addComment(request: AddCommentRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.addComment(
                MultipartBody.Part.createFormData("postId", request.postId!!),
                MultipartBody.Part.createFormData("description", request.description!!)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun reportPost(request: ReportPostRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.reportPost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun sharePost(request: SharePostRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.sharePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun savePost(request: SavePostRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.savePost(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun followUser(request: User): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.followUser(request)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getPostDetail(request: PostDetailRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            if(!checkIfHasNetwork())
                throw NoInternetException()
            baseResponse = apiService.getPostDetail(request.postId)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            }
        }
        return baseResponse
    }

    override suspend fun updatePostViewDuration(request: UpdatePostDurationRequest): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updatePostViewDuration(request)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is HttpException) {
                baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
                baseResponse.error?.status = e.code()
            }
        }
        return baseResponse
    }
}