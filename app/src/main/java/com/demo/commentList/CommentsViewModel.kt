package com.demo.commentList

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.request.RequestGetCommentsReplyList
import com.demo.model.request.RequestLikeUnLikeComment
import com.demo.model.request.RequestSaveComment
import com.demo.model.request.User
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseLikeUnLikeComment
import com.demo.model.response.ResponseSaveComment
import com.demo.model.response.UserComment
import com.demo.webservice.ApiRegister

class CommentsViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    var uploadingAudioUrl = ""

    val requestGetCommentsReplyList = ObservableField<RequestGetCommentsReplyList>()

    val requestLikeUnLikeComment = ObservableField<RequestLikeUnLikeComment>()
    val responseLikeUnLikeComment = MutableLiveData<MasterResponse<ResponseLikeUnLikeComment>>()
    val requestLikeUnLikeReply = ObservableField<RequestLikeUnLikeComment>()
    val responseLikeUnLikeReply = MutableLiveData<MasterResponse<ResponseLikeUnLikeComment>>()
    var position: Int = 0
    val requestSaveComment = ObservableField<RequestSaveComment>()
    var postId: Int = 0

    val responseSaveComment = MutableLiveData<MasterResponse<UserComment>>()

    init {
        requestLikeUnLikeComment.set(RequestLikeUnLikeComment())
        requestLikeUnLikeReply.set(RequestLikeUnLikeComment())
        requestSaveComment.set(RequestSaveComment())
        requestGetCommentsReplyList.set(RequestGetCommentsReplyList())
    }

    fun callSaveCommentApi() {
        baseRepo.restClient.callApi(
            ApiRegister.SAVECOMMENT,
            requestSaveComment.get()!!,
            responseSaveComment
        )
    }

    fun callLikeUnlikeCommentApi(position: Int) {
        this.position = position
        baseRepo.restClient.callApi(
            ApiRegister.LIKEUNLIKECOMMENT,
            requestLikeUnLikeComment.get()!!,
            responseLikeUnLikeComment
        )
    }

    fun startReply(comment: UserComment, pos: Int) {
        this.position = pos
        baseRepo.restClient.callApi(
            ApiRegister.SAVECOMMENT,
            requestLikeUnLikeComment.get()!!,
            responseLikeUnLikeComment
        )
    }

    lateinit var reply: UserComment
    fun callLikeUnlikeReplyApi(reply: UserComment, pos: Int) {
        this.reply = reply
        this.position = pos
        baseRepo.restClient.callApi(
            ApiRegister.LIKEUNLIKECOMMENT,
            requestLikeUnLikeReply.get()!!,
            responseLikeUnLikeReply
        )
    }

    var commentsData: List<UserComment> = emptyList()
    var areLimitedEntries = true
    val responseGetCommentsList = MutableLiveData<MasterResponse<List<UserComment>>>()
    /* fun callGetCommentsListApi() {
         val request = RequestGetCommentsList(postId = postId)
         baseRepo.restClient.callApi(ApiRegister.GETCOMMENTSLIST, request, responseGetCommentsList)
     }*/


    /**
     *  API Implemented 2 march
     */
    fun callGetCommentsReplyApi(postId: Int, userID: Int, pageNumber: Int, pageSize: Int) {
        val request = RequestGetCommentsReplyList(
            commentId = postId,
            userId = userID,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
        baseRepo.restClient.callApi(
            ApiRegister.GETCOMMENTSREPLYLIST,
            request,
            responseGetCommentsList
        )
    }
}