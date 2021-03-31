package com.demo.category_select

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestAddUserCategory
import com.demo.model.request.RequestGetCategory
import com.demo.model.response.GetCategory
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseAddUserCategory
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.webservice.ApiRegister

class CategoryViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    val requestAddUserCategory = ObservableField<RequestAddUserCategory>()
    val requestGetCategory = ObservableField<RequestGetCategory>()
    val responseGetCategory = MutableLiveData<MasterResponse<List<GetCategory>>>()
    val responseAddUserCategory = MutableLiveData<MasterResponse<ResponseAddUserCategory>>()

    var postCompleteGoBack = false

    fun callGetCategoryApi() {

        requestGetCategory.set(RequestGetCategory())
        baseRepo.restClient.callApi(ApiRegister.GETCATEGORY, requestGetCategory.get(), responseGetCategory)
    }

    fun callSaveUserCategoryApi(selectedIds : HashSet<Int>) {
        requestAddUserCategory.set(RequestAddUserCategory(Prefs.init().loginData!!.userId,selectedIds.joinToString(",")))
        baseRepo.restClient.callApi(ApiRegister.ADDUSERCATEGORY, requestAddUserCategory.get()!!, responseAddUserCategory)
    }

    fun parseBundle(arguments: Bundle?) {
        postCompleteGoBack = arguments?.getBoolean(ParcelKeys.PK_GO_BACK,false)?:false
    }

}