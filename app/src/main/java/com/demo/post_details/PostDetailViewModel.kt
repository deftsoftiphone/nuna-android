package com.demo.post_details

import android.os.Bundle
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import com.demo.model.Pinboard
import com.demo.model.request.*
import com.demo.model.response.*
import com.demo.util.ParcelKeys
import com.demo.util.Prefs
import com.demo.webservice.ApiRegister

class PostDetailViewModel(controller: AsyncViewController) : BaseViewModel(controller) {
    var uploadingAudioUrl = ""


    var postId: Int = 0
    val isUserBeingFollowed = ObservableBoolean(false)
    val isPostLiked = ObservableBoolean(false)
    val likeCount = ObservableInt(0)

    val requestSaveComment = ObservableField<RequestSaveComment>()
    val requestGetCommentsList = ObservableField<RequestGetCommentsList>()
    val requestGetCommentsReplyList = ObservableField<RequestGetCommentsReplyList>()

    var isMyPost = MutableLiveData<Boolean>(false)
    val responseGetPostDetails = MutableLiveData<MasterResponse<ResponseGetPostDetails>>()
    val responseSaveComment = MutableLiveData<MasterResponse<ResponseSaveComment>>()
    val responseGetCommentsList = MutableLiveData<MasterResponse<List<UserComment>>>()
    val responseGetCommentsReplyList = MutableLiveData<MasterResponse<List<UserComment>>>()
    val responseFollowUnfollowUser = MutableLiveData<MasterResponse<RequestFollowUnfollowUser>>()
    val responseLikeUnLikePost = MutableLiveData<MasterResponse<ResponseLikeUnLikePost>>()

    val responseLikeUnLikePostTwo = MutableLiveData<MasterResponse<ResponseLikeUnLikePost>>()
    val responseRelatedPosts = MutableLiveData<MasterResponse<List<Post>>>()
    val responseBoardList = MutableLiveData<MasterResponse<List<Pinboard>>>()
    val responseAddPostToPinboard = MutableLiveData<MasterResponse<Any>>()
    val responseActionOnPost = MutableLiveData<MasterResponse<ResponseActionOnPost>>()
    lateinit var conte:FragmentActivity

    var showAllComments = MutableLiveData<Boolean>(false)

    init {
        requestSaveComment.set(RequestSaveComment())
    }

    fun getSeeMoreCommentText(data: List<UserComment>?): String {
        if (data == null || data.size <= 3) return ""
        val size = data.size - 3
        return conte.resources.getString(R.string.see_more_comments, size)
    }

    fun getCommentsHeading(data: List<UserComment>?): String {
        if (data == null || data.isEmpty()) return ""
        val size = data.size
        return conte.resources.getString(R.string.x_comments, size)
    }

    fun parseBundle(b: Bundle?) {
        b?.apply {
            postId = getInt(ParcelKeys.PK_POST_ID)
        }
    }

    fun callGetPostDetailsApi() {
        val request = RequestGetPostDetails(postId = postId)
        baseRepo.restClient.callApi(ApiRegister.GETPOSTDETAILS, request, responseGetPostDetails)
    }

    fun callSaveCommentApi() {
        baseRepo.restClient.callApi(
            ApiRegister.SAVECOMMENT,
            requestSaveComment.get()!!,
            responseSaveComment
        )
    }

    fun callGetCommentsListApi() {
        val request = RequestGetCommentsList(postId = postId)
        baseRepo.restClient.callApi(ApiRegister.GETCOMMENTSLIST, request, responseGetCommentsList)
    }

    var position: Int = 0
    fun callGetCommentsReplylistApi(position: Int) {
        this.position = position
        baseRepo.restClient.callApi(
            ApiRegister.GETCOMMENTSREPLYLIST,
            requestGetCommentsReplyList.get()!!,
            responseGetCommentsReplyList
        )
    }

    fun callFollowUnfollowUserApi(toBeFollowedUserId: Int, follow: Boolean) {
        val request = RequestFollowUnfollowUser()

        request.userId=toBeFollowedUserId
        request.followerUserId= Prefs.init().loginData!!.userId
        request.follow=follow

        baseRepo.restClient.callApi(
            ApiRegister.FOLLOWUNFOLLOWUSER,
            request,
            responseFollowUnfollowUser
        )
    }
    fun callLikeUnlikePostApi(postId: Int, postLiked: Boolean) {
        val request = RequestLikeUnLikePost(postId, postLiked)
        baseRepo.restClient.callApi(ApiRegister.LIKEUNLIKEPOST, request, responseLikeUnLikePostTwo)
    }
    fun callLikeUnlikePostApi() {
        val post = responseGetPostDetails.value?.data?.post!!
        val request = RequestLikeUnLikePost(post.postId, !post.postLiked)
        baseRepo.restClient.callApi(ApiRegister.LIKEUNLIKEPOST, request, responseLikeUnLikePost)
    }

    fun callRelatedPostsApi() {
        val request =
            RequestGetPostList(targetUserId = responseGetPostDetails.value?.data?.post?.user?.userId!!)
        baseRepo.restClient.callApi(ApiRegister.GETPOSTLIST, request, responseRelatedPosts)
    }

    fun callGetBoardList() {
        baseRepo.restClient.callApi(
            ApiRegister.GET_USER_PINBOARD_LIST,
            RequestGetUserBoards(pageSize = 100),
            responseBoardList
        )
    }

    fun addPostToAPinboard(postId: Int, pinboardId: Int) {
        val request =
            RequestAddOrRemovePinboardPost(action = "add", postId = postId, pinboardId = pinboardId)
        baseRepo.restClient.callApi(
            ApiRegister.ADD_OR_REMOVE_PINBOARD_POST,
            request,
            responseAddPostToPinboard
        )
    }

    fun callReportApi() {
        val request = RequestActionOnPost(responseGetPostDetails.value!!.data!!.post!!.postId)
        baseRepo.restClient.callApi(ApiRegister.ACTION_ON_POST, request, responseActionOnPost)
    }


    fun callShareApi() {
        val request = RequestSharePost(responseGetPostDetails.value!!.data!!.post!!.postId)
        baseRepo.restClient.callApi(ApiRegister.sharePost, request, responseActionOnPost)
    }


}