package com.demo.postList

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestActionOnPost
import com.demo.model.request.RequestGetPostList
import com.demo.model.response.MasterResponse
import com.demo.model.response.Post
import com.demo.model.response.ResponseActionOnPost
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.webservice.ApiRegister

class PostsViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    companion object {
        const val LAYOUT_TYPE_VERTICAL = 3
        const val LAYOUT_TYPE_HORIZONTAL = 4
    }

    var layoutType = 0
    val responseGetPostList_NEW = MutableLiveData<MasterResponse<List<Post>>>()
    val responseGetPostList_PAGING = MutableLiveData<MasterResponse<List<Post>>>()
    var lastRequestGetPost = MutableLiveData<RequestGetPostList>()
    val responseActionOnPost = MutableLiveData<MasterResponse<ResponseActionOnPost>>()

    init {
        lastRequestGetPost.value = RequestGetPostList()
    }

    fun parseBundle(arguments: Bundle?) {
        layoutType = arguments?.getInt(ParcelKeys.PK_LIST_TYPE, 0) ?: 0
    }

    private fun callGetPostList(
        responseHolder: MutableLiveData<MasterResponse<List<Post>>>,
        showProgress: Boolean
    ) {
        baseRepo.restClient.callApi(
            ApiRegister.GETPOSTLIST,
            lastRequestGetPost.value,
            responseHolder,
            showProgress
        )
    }

    fun loadNextPage() {
        lastRequestGetPost.value!!.pageNumber++

        lastRequestGetPost.value?.apply {
            targetUserId = Prefs.init().loginData!!.userId
        }


        callGetPostList(responseGetPostList_PAGING, false)
    }

    fun loadFirstPage() {
        lastRequestGetPost.value = RequestGetPostList().apply {
            targetUserId = Prefs.init().loginData!!.userId
        }
        callGetPostList(responseGetPostList_NEW, true)
    }

    fun deletePost(postIda: Int) {
        val request = RequestActionOnPost().apply {
            postId = postIda
            action = "delete"
            userId = Prefs.init().loginData!!.userId
        }
        baseRepo.restClient.callApi(ApiRegister.ACTION_ON_POST, request, responseActionOnPost)
    }

}