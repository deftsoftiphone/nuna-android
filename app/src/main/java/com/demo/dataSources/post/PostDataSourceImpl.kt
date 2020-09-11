package com.demo.dataSources.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.request.RequestSavePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.customError.CustomErrors
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService

class PostDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider,
    private val customErrors: CustomErrors
) : PostDataSource {
    override suspend fun getCategories(
        queryParams: QueryParams
    ): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getCategories(
                queryParams.offset,
                queryParams.limit,
                queryParams.search,
                queryParams.languageId!!
            ).await()

            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }

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
            baseResponse.error?.message = customErrors.postErrorMsg(baseResponse.error?.message)
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
}