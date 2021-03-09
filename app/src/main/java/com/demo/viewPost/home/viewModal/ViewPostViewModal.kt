package com.demo.viewPost.home.viewModal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.search.SearchRepository
import com.demo.repository.viewPost.ViewPostRepository
import com.demo.util.Prefs
import com.demo.util.Util.Companion.checkIfHasNetwork
import com.demo.util.plusAssign
import com.demo.viewPost.clickhandler.CommentListener
import com.demo.viewPost.clickhandler.PostUpdateListener
import com.demo.viewPost.clickhandler.ViewPostClickHandler
import com.demo.viewPost.home.adapter.CommentsAdapter
import com.demo.viewPost.home.adapter.ViewPostRecyclerAdapter
import com.demo.webservice.APIService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewPostViewModal(
    private val viewPostRepository: ViewPostRepository,
    private val homeRepository: HomeRepository,
    private val searchRepository: SearchRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    var postliked = true
    var mClickHandler = ClickHandler()
    lateinit var mPostAdapter: ViewPostRecyclerAdapter
    var mCommentAdapter: CommentsAdapter
    var comments = MutableLiveData<ArrayList<Comment>>(ArrayList())
    val removePost = MutableLiveData(-1)
    private var posts = MutableLiveData<ArrayList<PostAssociated>>()
    private var postUpdateListener: PostUpdateListener? = null
    var clickedUserId = MutableLiveData("")
    var clickedHashTag = MutableLiveData<HashTag>(HashTag())
    private var commentListener: CommentListener? = null
    var allCommentsLoaded = false
    var queryParams = QueryParams().apply {
        limit = 15
    }

    var noPostFound = MutableLiveData(false)

    init {
        mCommentAdapter = CommentsAdapter(R.layout.item_comments, mClickHandler)
    }

    var postViewDurationRequest = UpdatePostDurationRequest()

    fun setCommentListener(commentListener: CommentListener) {
        this.commentListener = commentListener
    }

    fun setPostUpdateListener(postUpdateListener: PostUpdateListener) {
        this.postUpdateListener = postUpdateListener
    }

    fun setPosts(posts: ArrayList<PostAssociated>) {
        this.posts.value = posts
    }

    fun getPosts(): MutableLiveData<ArrayList<PostAssociated>> {
        return posts
    }

    fun likePost(postId: String, position: Int) {
        AndroidNetworking.forceCancel("GetPostDetail")
        if (checkIfHasNetwork()) {
            viewModelScope.launch {
                viewPostRepository.likePost(
                    LikePostRequest(postId),
                    onResult = { isSuccess: Boolean, response ->
                        if (isSuccess) {
                            response.data?.postDetails?.let {
                                postUpdateListener?.onPostUpdate(it, position)
                            }
                        } else response.error?.message?.let { toastMessage.postValue(it) }
                    })
            }
        } else toastMessage.postValue(resources.getString(R.string.connectErr))
    }

    fun savePost(postId: String, position: Int) {
        AndroidNetworking.forceCancel("GetPostDetail")
        viewModelScope.launch {
            viewPostRepository.savePost(SavePostRequest(postId)) { isSuccess, response ->
                if (isSuccess) {
                    response.data?.postDetails?.let {
                        postUpdateListener?.onPostUpdate(it, position)
                    }
                } else response.error?.message?.let { toastMessage.postValue(it) }
            }
        }
    }

    fun addComment(postId: String, description: String) {
        viewModelScope.launch {
            viewPostRepository.addComment(
                AddCommentRequest(postId, description.trim()),
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                        commentListener?.onPostComment()
                    } else toastMessage.postValue(response.error?.message!!)
                })
        }
    }

    fun addChildComment(postId: String, description: String, childCommentId: String) {
        viewModelScope.launch {
            viewPostRepository.addComment(
                AddCommentRequest(postId, description, childCommentId),
                onResult = { isSuccess, response ->
                    if (isSuccess) {

                    } else toastMessage.postValue(response.error?.message!!)
                })
        }
    }

    fun reportPost(postId: String) {
        viewModelScope.launch {
            viewPostRepository.reportPost(
                ReportPostRequest(postId),
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                        response.message?.let { toastMessage.postValue(it) }
                    } else response.error?.message?.let { toastMessage.postValue(it) }
                })
        }
    }

    fun getPostComment(offset: Int) {
        viewModelScope.launch {
            queryParams.offset = offset
            viewPostRepository.getPostComments(queryParams,
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                        GlobalScope.launch(Dispatchers.Main) {
                            response.data?.commentList?.let {
                                if (it.isNotEmpty()) {
                                    comments += it
                                    allCommentsLoaded = false
                                    getPostDetail(queryParams.postId!!)
                                } else allCommentsLoaded = true
                            }
                        }
                    } else comments.postValue(ArrayList())
                })
        }
    }

    fun likeComment(commentId: String, position: Int) {
        viewModelScope.launch {
            viewPostRepository.likeComment(
                LikeCommentRequest(commentId, "comment"),
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                        GlobalScope.launch(Dispatchers.Main) {
                            response.data?.commentDetails?.let {
                                mCommentAdapter.likeUnlikeComment(
                                    it,
                                    position
                                )
                            }
                        }
                    }
//                else toastMessage.postValue(response.error?.message)
                })
        }
    }

    fun followUser(userId: String, position: Int) {
        AndroidNetworking.forceCancel("GetPostDetail")
        viewModelScope.launch {
            viewPostRepository.followUser(User(userId = userId),
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                        postUpdateListener?.onFollowUpdate(userId)
                    } else response.error?.message?.let { toastMessage.postValue(it) }
                })
        }
    }

    fun getPostDetail(postId: String) {
        AndroidNetworking.forceCancel("GetPostDetail")
        AndroidNetworking.get("${APIService.BASE_URL}${APIService.API_VERSION}api/posts/postDetails/{id}")
            .setTag("GetPostDetail")
            .addPathParameter("id", postId)
            .addHeaders("Accept", "application/json")
            .addHeaders("x-access-token", "Bearer $accessToken")
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsObject(
                BaseResponse::class.java,
                object : ParsedRequestListener<BaseResponse?> {
                    override fun onResponse(response: BaseResponse?) {
                        response?.data?.postDetail?.let {
                            println("postDetail = ${Gson().toJson(it)}")
                            postUpdateListener?.onPostUpdate(it, -1)
                        }
                    }

                    override fun onError(anError: ANError) {
                        val errorCode = anError.errorCode
                        if (errorCode == 449)
                            noPostFound.postValue(true)
//                        toastMessage.postValue(anError.message)
                    }
                })
    }

    fun getCurrentOffset(): Int {
        return comments.value?.size!!
    }

    inner class ClickHandler : ViewPostClickHandler {
        override fun onLikeClick(model: PostAssociated, position: Int) {
            model.id?.let {
                likePost(it, position)
            }
        }

        override fun onCommentClick(model: PostAssociated, position: Int) {
            model.id?.let {
                queryParams.postId = model.id
                comments.value?.clear()
                getPostComment(0)
            }
        }

        override fun onUserFollowClick(model: PostAssociated, position: Int) {
            model.createdBy?.id?.let {
                followUser(it, position)
            }
        }

        override fun onUnLikeClick(model: PostAssociated, position: Int) {
            model.id?.let {
                likePost(it, position)
            }
        }

        override fun onSaveClick(model: PostAssociated, position: Int) {
            model.id?.let {
                savePost(it, position)
            }
        }

        override fun onRemoveSaveClick(model: PostAssociated, position: Int) {
            model.id?.let {
                savePost(it, position)
            }
        }

        override fun onShareClick(model: PostAssociated, position: Int) {

        }

        override fun onUserClick(id: String) {
            clickedUserId.postValue(id)
        }

        override fun onReportClick(model: PostAssociated, position: Int) {
            if (model.createdBy?.id == Prefs.init().currentUser?.id)
                toastMessage.postValue(resources.getString(R.string.cant_report_own_post))
            else reportPost(model.id!!)
        }

        override fun onDeleteClick(model: PostAssociated, position: Int) {
            deletePost(model.id!!, position)
        }

        override fun onCommentLikeClick(comment: Comment, position: Int) {
            likeComment(comment.id, position)
        }
    }

    fun sharePost(id: String, position: Int) {
        viewModelScope.launch {
            viewPostRepository.sharePost(SharePostRequest(post = id)) { isSuccess, response ->
                if (isSuccess) {
                    response.data?.postDetails?.let {
                        postUpdateListener?.onPostUpdate(it, position)
                    }
                } else toastMessage.postValue(response.error?.message!!)
            }
        }
    }

    fun deletePost(id: String, index: Int) {
        queryParams.postId = id
        showLoading.postValue(true)
        homeRepository.removePost(queryParams) { isSuccess, baseResponse ->
            showLoading.postValue(false)
            if (isSuccess) {
                removePost.postValue(index)
            } else baseResponse.error?.message?.let { toastMessage.postValue(it) }
        }
    }

    fun updatePostViewDuration() {
        viewModelScope.launch {
            viewPostRepository.updatePostViewDuration(postViewDurationRequest,
                onResult = { isSuccess, response ->
                    if (isSuccess) {
                    }
                })
        }
    }

    //Update code for pagination
    var queryParameter = QueryParams()
    var url: String? = null
    val accessToken: String = Prefs.init().currentToken
    var showPaginationLoading = MutableLiveData<Boolean>().apply { value = false }

    fun getMorePosts() {
        queryParameter.offset = posts.value?.size
        queryParameter.limit = 15
        showPaginationLoading.postValue(true)
        AndroidNetworking.forceCancel("videosListRequest")
        println("pagination url = ${url} Query_param = ${Gson().toJson(queryParameter)}")
        AndroidNetworking.get(url)
            .setTag("videosListRequest")
            .addQueryParameter(queryParameter)
            .addHeaders("Accept", "application/json")
            .addHeaders("x-access-token", "Bearer $accessToken")
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsObject(
                BaseResponse::class.java,
                object : ParsedRequestListener<BaseResponse?> {
                    override fun onResponse(response: BaseResponse?) {
                        showPaginationLoading.postValue(false)
                        successAPI(response)
                    }

                    override fun onError(anError: ANError) {
                        showPaginationLoading.postValue(false)
                        errorAPI(anError)
                    }
                })
    }

    private fun errorAPI(anError: ANError) {
        println("pagination error = ${anError}")
//        anError.message?.let { toastMessage.postValue(it) }
    }

    private fun successAPI(response: BaseResponse?) {
        val tempList = posts.value
        tempList?.let { postsList ->

            response?.data?.posts?.let {
                if (it.isNotEmpty()) {
                    postsList.addAll(it)
                    setPosts(postsList)
                }
            }

            response?.data?.userProfileBoard?.let {
                if (it.isNotEmpty()) {
                    postsList.addAll(it)
                    setPosts(postsList)
                }
            }

            response?.data?.videoPosts?.let {
                if (it.isNotEmpty()) {
                    postsList.addAll(it)
                    setPosts(postsList)
                }
            }

            response?.data?.followingPosts?.let {
                if (it.isNotEmpty()) {
                    postsList.addAll(it)
                    setPosts(postsList)
                }
            }

            response?.data?.popularPosts?.let {
                if (it.isNotEmpty()) {
                    postsList.addAll(it)
                    setPosts(postsList)
                }
            }
        }

    }
}