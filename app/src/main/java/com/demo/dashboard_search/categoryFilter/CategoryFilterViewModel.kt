package com.demo.dashboard_search.categoryFilter

import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestGetCategory
import com.demo.model.response.GetCategory
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetCategory
import com.demo.webservice.ApiRegister

class CategoryFilterViewModel(controller: AsyncViewController) : BaseViewModel(controller){

    val responseGetCategory = MutableLiveData<MasterResponse<List<GetCategory>>>()

    fun callGetCategoryListApi(){
        val request = RequestGetCategory()
        baseRepo.restClient.callApi(ApiRegister.GETCATEGORY, request,responseGetCategory)
    }
}