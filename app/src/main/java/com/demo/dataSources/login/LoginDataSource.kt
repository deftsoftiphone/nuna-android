package com.demo.dataSources.login

import androidx.lifecycle.LiveData
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.User

interface LoginDataSource {
    suspend fun getLanguages(): LiveData<BaseResponse>
    suspend fun updateLanguage(payLoad:Language): LiveData<BaseResponse>
    suspend fun login(payload: RequestUserRegister): LiveData<BaseResponse>
    suspend fun verifyOTP(payload: RequestValidate): LiveData<BaseResponse>
    suspend fun logout(): BaseResponse
}