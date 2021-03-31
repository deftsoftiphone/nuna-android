package com.demo.dataSources.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.providers.customError.CustomErrors
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService

class LoginDataSourceImpl(
    private val apiService: APIService,
    private val resources: ResourcesProvider,
    private val customErrors: CustomErrors
) :
    LoginDataSource {
    override suspend fun getLanguages(): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.getLanguages()
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun updateLanguage(payLoad: Language): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.updateLanguage(payLoad)
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun login(payload: RequestUserRegister): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.login(payload)
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun verifyOTP(payload: RequestValidate): LiveData<BaseResponse> {
        val res = MutableLiveData<BaseResponse>()
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.verifyOTP(payload)
            res.postValue(baseResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
            baseResponse.error?.message = customErrors.otpErrorMsg(baseResponse.error?.message)
            res.postValue(baseResponse)
        }
        return res
    }

    override suspend fun logout(): BaseResponse {
        var baseResponse = BaseResponse()
        try {
            baseResponse = apiService.logout()
        } catch (e: Exception) {
            e.printStackTrace()
            baseResponse.error = APIService.getErrorMessageFromGenericResponse(e, resources)
        }
        return baseResponse
    }
}