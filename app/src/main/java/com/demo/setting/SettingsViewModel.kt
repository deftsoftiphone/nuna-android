package com.demo.setting


import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.base.ParentViewModel
import com.demo.model.request.RequestLogin
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLogin
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.login.LoginRepository
import com.demo.util.Prefs
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val loginRepository: LoginRepository,
    private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    val errEmail = ObservableField<String>()
    val errPassword = ObservableField<String>()

    val requestLogin = ObservableField<RequestLogin>()
    val responseLogin = MutableLiveData<MasterResponse<ResponseLogin>>()
    var logout = MutableLiveData(false)

    init {
        if (Prefs.init().rememberMe) {
            requestLogin.set(Prefs.init().userCreds)
        } else {
            requestLogin.set(RequestLogin())
        }
    }

    fun isLoginDataValid(): Boolean {

      /*  val data = requestLogin.get() as RequestLogin
        if (!Validator.isEmailValid(data.email, errEmail)) {
            MasterResponse<ResponseLogin>()
            return false

        } else if (!Validator.isPasswordValid(data.password, errPassword)) {
            return false
        }*/
        return true
    }

    fun logoutUser() {
        showLoading.postValue(true)
        loginRepository.logout { isSuccess, baseResponse ->
            showLoading.postValue(false)
            if (isSuccess)
                logout.postValue(isSuccess)
            else toastMessage.postValue(baseResponse.message)
        }
    }

    fun updateNotificationStatus(status:Boolean) {
            viewModelScope.launch {
                homeRepository.updateNotificationStatus(status) { isSuccess, response ->
                    if (isSuccess) {
                        Prefs.init().notificationAll = !Prefs.init().notificationAll
                    }
                }
            }
    }

}