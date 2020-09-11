package com.demo.repository.login

import androidx.lifecycle.LiveData
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams

interface LoginRepository {
    suspend fun getLanguages(): LiveData<BaseResponse>
    suspend fun updateLanguage(payload: Language): LiveData<BaseResponse>
    suspend fun login(payload: RequestUserRegister): LiveData<BaseResponse>
    suspend fun verifyOTP(payload: RequestValidate): LiveData<BaseResponse>

    fun logout(
        onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit
    )
}