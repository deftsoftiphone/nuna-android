package com.demo.language_select

import android.service.voice.AlwaysOnHotwordDetector
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.demo.model.request.Language
import com.demo.model.request.RequestLanguage
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLanguage
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.repository.login.LoginRepository
import com.demo.webservice.Resource
import kotlinx.coroutines.Dispatchers

class LanguageSelectViewModel(
//    controller: AsyncViewController,
    private val loginRepository: LoginRepository
) : ViewModel() {

    val errEmail = ObservableField<String>()
    val errPassword = ObservableField<String>()


    val requestLanguage = ObservableField<RequestLanguage>()
    val responseLanguage = MutableLiveData<MasterResponse<ResponseLanguage>>()

    init {
        requestLanguage.set(RequestLanguage())
    }

    suspend fun getLanguage(): LiveData<BaseResponse> {
        return loginRepository.getLanguages()
    }

    suspend fun updateLanguage(payload: Language): LiveData<BaseResponse> {
        return loginRepository.updateLanguage(payload)
    }
}