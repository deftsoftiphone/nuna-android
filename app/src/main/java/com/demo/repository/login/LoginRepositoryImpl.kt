package com.demo.repository.login

import androidx.lifecycle.LiveData
import com.demo.dataSources.login.LoginDataSource
import com.demo.model.request.Language
import com.demo.model.request.RequestUserRegister
import com.demo.model.request.RequestValidate
import com.demo.model.response.baseResponse.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginRepositoryImpl(private val loginDataSource: LoginDataSource) :
    LoginRepository {
    override suspend fun getLanguages(): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext loginDataSource.getLanguages()
        }
    }

    override suspend fun updateLanguage(payload: Language): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext loginDataSource.updateLanguage(payload)
        }    }

    override suspend fun login(payload: RequestUserRegister): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext loginDataSource.login(payload)
        }
    }

    override suspend fun verifyOTP(payload: RequestValidate): LiveData<BaseResponse> {
        return withContext(Dispatchers.IO) {
            return@withContext loginDataSource.verifyOTP(payload)
        }
    }

    override fun logout(onResult: (isSuccess: Boolean, baseResponse: BaseResponse) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = loginDataSource.logout()
            onResult(response.success!!, response)
        }
    }
}