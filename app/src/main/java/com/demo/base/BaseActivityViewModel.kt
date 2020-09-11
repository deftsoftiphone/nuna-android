package com.demo.base

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLogin
import com.demo.webservice.ApiRegister


class BaseActivityViewModel(viewController : AsyncViewController) : BaseViewModel(viewController){

    val progressDialogStatus : MutableLiveData<String> = MutableLiveData()
    val alertDialogController : MutableLiveData<String> = MutableLiveData()
    val keyboardController : MutableLiveData<Boolean> = MutableLiveData()
    var alertDialogSpecs : AlertDialogSpecs = AlertDialogSpecs()
    val responseLogOut : MutableLiveData<MasterResponse<ResponseLogin>> = MutableLiveData()
    var parcels : HashMap<Int, Bundle> = HashMap()

    fun logOut(){
        baseRepo.restClient.callApi(ApiRegister.USERAUTH, null, responseLogOut)
    }
}