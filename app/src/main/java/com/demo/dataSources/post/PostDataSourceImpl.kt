package com.demo.dataSources.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.AddPostRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.util.NoInternetException
import com.demo.util.Util
import com.demo.webservice.APIService

class PostDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider
) : PostDataSource {

    override suspend fun getHashTags(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getHashTags(
                queryParams.offset,
                queryParams.limit,
                queryParams.search,
                queryParams.startsWith,
                queryParams.languageId!!,
                queryParams.categoryId
            ).await()

            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
//            baseResponse.error?.message = customErrors.postErrorMsg(baseResponse.error?.message)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun createPost(requestSavePost: RequestSavePost): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.createPost(
                requestSavePost.languageId!!,
                requestSavePost.categoryId!!,
                requestSavePost.description!!,
                requestSavePost.image,
                requestSavePost.video!!
            ).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun createPostV2(request: AddPostRequest): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.createPostV2(request).await()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }


    override suspend fun getCategories(params: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getPostCategories(params.offset, params.limit)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }

    override suspend fun getHashTag(queryParams: QueryParams): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            if (!Util.checkIfHasNetwork())
                throw NoInternetException()
            baseResponse = apiService.getHashTag(
                queryParams.offset,
                queryParams.limit,
                queryParams.search,
                queryParams.startsWith,
                queryParams.languageId!!,
                queryParams.categoryId
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }
}