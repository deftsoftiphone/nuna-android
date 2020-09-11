package com.demo.view_others_profile

 import android.os.Bundle
 import android.util.Log
 import androidx.lifecycle.MutableLiveData
 import com.demo.base.AsyncViewController
 import com.demo.base.BaseViewModel
 import com.demo.model.Pinboard
 import com.demo.model.request.RequestGetPostList
 import com.demo.model.request.RequestGetUserProfile
 import com.demo.model.request.RequestLikeUnLikePost
 import com.demo.model.response.MasterResponse
 import com.demo.model.response.Post
 import com.demo.model.response.ResponseGetProfile
 import com.demo.model.response.ResponseLikeUnLikePost
 import com.demo.util.ParcelKeys
 import com.demo.webservice.ApiRegister

class OthersProfileViewModel(controller: AsyncViewController) : BaseViewModel(controller) {
    var targetProfileId = 0

    var position: Int = 0
    val responseGetPostList_NEW = MutableLiveData<MasterResponse<List<Post>>>()
    val responseGetPostList_PAGING = MutableLiveData<MasterResponse<List<Post>>>()
    var lastRequestGetPost = MutableLiveData<RequestGetPostList>()


    var requestGetUserProfile = MutableLiveData<RequestGetUserProfile>()


    val pagerProgress = MutableLiveData<Boolean>(false)
    var isPageLoading = false
    var loadedAllPages = false

    val responseLikeUnLikePost = MutableLiveData<MasterResponse<ResponseLikeUnLikePost>>()

    val responseGetProfile = MutableLiveData<MasterResponse<ResponseGetProfile>>()
    val responseAddPinboard = MutableLiveData<MasterResponse<Pinboard>>()

    init {
        requestGetUserProfile.value = RequestGetUserProfile()
        lastRequestGetPost.value = RequestGetPostList()
    }

    fun callGetProfileApi() {


        requestGetUserProfile.value?.apply {
            userId=targetProfileId
        }

        baseRepo.restClient.callApi(
            ApiRegister.GETUSERPROFILE,
            requestGetUserProfile.value,
            responseGetProfile
        )
    }

    fun parseBundle(bundle : Bundle?){
        targetProfileId = bundle?.getInt(ParcelKeys.PK_PROFILE_ID) ?: targetProfileId
    }

    fun callGetPostList(
        responseHolder: MutableLiveData<MasterResponse<List<Post>>>,
        showProgress: Boolean) {

        baseRepo.restClient.callApi(
            ApiRegister.GETPOSTLIST,
            lastRequestGetPost.value,
            responseHolder,
            showProgress
        )
    }

    /*
  * calls api to get document list based on text search or date search
  * */
    fun initiateSearchQuery() {

        lastRequestGetPost.value?.apply {
            searchKeyWord = ""
            pageNumber = 0
            targetUserId=targetProfileId
        }
        controller?.hideKeyboard()
        callGetPostList(responseGetPostList_NEW, true)
    }

    /*
    * calls api with same params as last , but with next page query
    * result should be added in adapter
    * */
    fun loadNextPage() {
        pagerProgress.value = true
        isPageLoading = true
        lastRequestGetPost.value!!.pageNumber++
        lastRequestGetPost.value!!.targetUserId=targetProfileId
        Log.d("PAGINGGG", lastRequestGetPost.value.toString())
        callGetPostList(responseGetPostList_PAGING, false)
    }

    fun callLikeUnlikePostApi(postId: Int, postLiked: Boolean) {
        val request = RequestLikeUnLikePost(postId, postLiked)
        baseRepo.restClient.callApi(ApiRegister.LIKEUNLIKEPOST, request, responseLikeUnLikePost)
    }

}