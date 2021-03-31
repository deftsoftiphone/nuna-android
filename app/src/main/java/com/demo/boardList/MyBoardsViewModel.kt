package com.demo.boardList

import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.Pinboard
import com.demo.model.request.*
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLikeUnLikePost
import com.demo.webservice.ApiRegister

class MyBoardsViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    val lastRequestGetUserBoards = MutableLiveData<RequestGetUserBoards>()
    val responseGetUserBoards = MutableLiveData<MasterResponse<List<Pinboard>>>()
    val responseEditPinboard = MutableLiveData<MasterResponse<Pinboard>>()
    val responseDeletePinboard = MutableLiveData<MasterResponse<Pinboard>>()
    val responseRemovePostFromPinboard = MutableLiveData<MasterResponse<Any?>>()
    var lastRequestRemovePinboardPost : RequestAddOrRemovePinboardPost?=null
    var position: Int = 0
    val responseLikeUnLikePost = MutableLiveData<MasterResponse<ResponseLikeUnLikePost>>()

    val errNoData = MutableLiveData<Boolean>(false)

    init {
        lastRequestGetUserBoards.value = RequestGetUserBoards()
    }

    fun callGetUserBoardList(request: RequestGetUserBoards?) {
        lastRequestGetUserBoards.value = request
        if (lastRequestGetUserBoards.value == null) {
            lastRequestGetUserBoards.value = RequestGetUserBoards()
        }
        baseRepo.restClient.callApi(
            ApiRegister.GET_USER_PINBOARD_LIST,
            lastRequestGetUserBoards.value,
            responseGetUserBoards
        )
    }

    fun callEditPinboardNameApi(pinBoardId: Int, newName: String) {
        baseRepo.restClient.callApi(
            ApiRegister.ADD_PINBOARD,
            RequestAddPinboard(newName, pinBoardId),
            responseEditPinboard
        )
    }

    fun callDeletePinboardApi(pinBoardId: Int) {
        baseRepo.restClient.callApi(
            ApiRegister.DELETE_PINBOARD,
            RequestDeletePinboard(pinBoardId),
            responseDeletePinboard
        )
    }

    fun callRemovePostFromBoardApi(postId: Int, pinBoardId: Int) {
        lastRequestRemovePinboardPost = RequestAddOrRemovePinboardPost("delete", pinBoardId, postId)
        baseRepo.restClient.callApi(
            ApiRegister.ADD_OR_REMOVE_PINBOARD_POST,
            lastRequestRemovePinboardPost,
            responseRemovePostFromPinboard
        )
    }

    fun callLikeUnlikePostApi(postId: Int, postLiked: Boolean) {
        val request = RequestLikeUnLikePost(postId, postLiked)
        baseRepo.restClient.callApi(ApiRegister.LIKEUNLIKEPOST, request, responseLikeUnLikePost)
    }

}