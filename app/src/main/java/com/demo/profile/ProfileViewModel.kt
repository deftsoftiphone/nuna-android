package com.demo.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.Pinboard
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetProfile
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.util.Util
import com.demo.util.plusAssign
import com.demo.util.runWithDelay

class ProfileViewModel(
    private val homeRepository: HomeRepository,
    val resource: ResourcesProvider
) : ParentViewModel() {
    var clickHandler: NewProfileFragment.ClickHandler? = null
    val responseGetProfile = MutableLiveData<MasterResponse<ResponseGetProfile>>()
    val responseAddPinboard = MutableLiveData<MasterResponse<Pinboard>>()
    val removePost = MutableLiveData(-1)
    val queryParams = QueryParams()
    var boardQueryParams = QueryParams()
    var boardLoaded = false
    var postsLoaded = false
    var userPosts = ArrayList<PostAssociated>()
    var userBoard = MutableLiveData<ArrayList<PostAssociated>>(ArrayList())
    var currentTab = 0
    var userPostParams = QueryParams()
    var showNoBoards = false
    var boardAlreadyLoading = false
    var postAlreadyLoading = false
    var loadBoard = true
    var showBoardSwipeLoader = MutableLiveData(false)

    init {
        boardQueryParams.apply {
            limit = resource.getInt(R.integer.query_param_limit_value)
            offset = 0
        }

        userPostParams.apply {
            limit = resource.getInt(R.integer.query_param_limit_value)
            offset = 0
        }
    }

    suspend fun getUserDetails(): LiveData<BaseResponse> {
        return homeRepository.getUserDetails()
    }

    suspend fun getOtherUserDetails(id: String?): LiveData<BaseResponse> {
        queryParams.id = id
        return homeRepository.getOtherUserDetails(queryParams)
    }

    suspend fun getUserPosts(offset: Int): LiveData<BaseResponse> {
        if (offset == 0) userPosts.clear()
        userPostParams.offset = offset
        return homeRepository.getUserPosts(userPostParams)
    }

    suspend fun getOtherUserPosts(id: String?, offset: Int): LiveData<BaseResponse> {
        queryParams.id = id
        queryParams.limit = resource.getInt(R.integer.query_param_limit_value)
        queryParams.offset = offset
        if (offset == 0) userPosts.clear()
        return homeRepository.getOtherUserPosts(queryParams)
    }

    suspend fun followUser(user: User): LiveData<BaseResponse> {
        return homeRepository.followUser(user)
    }

    fun removePost(id: String, index: Int) {
        queryParams.postId = id
        showLoading.postValue(true)
        homeRepository.removePost(queryParams) { isSuccess, baseResponse ->
            showLoading.postValue(false)
            if (isSuccess) {
                removePost.postValue(index)
            } else if (!Util.checkIfHasNetwork())
                baseResponse.error?.message?.let { toastMessage.postValue(it) }
        }
    }

    fun getUserBoard(offset: Int) {
        if (loadBoard) {
            loadBoard = false
            if (offset == 0) {
                userBoard.postValue(ArrayList())
                showNoBoards = false
            }
            boardAlreadyLoading = true
            queryParams.offset = offset
            queryParams.limit = resource.getInt(R.integer.query_param_limit_value)
            homeRepository.getUserBoard(queryParams) { isSuccess, response ->
                if (isSuccess) {
                    response.data?.userProfileBoard?.let {
                        if (it.isNotEmpty()) {
                            userBoard += it
                            if (it.size < 10)
                                boardLoaded = true
                            boardLoaded = false
                        } else {
                            boardLoaded = true
                        }
                    }
                } else if (!Util.checkIfHasNetwork())
                    response.error?.message?.let { toastMessage.postValue(it) }

                if (userBoard.value!!.isEmpty() && currentTab == 1) {
                    userBoard.postValue(ArrayList())
                    showNoBoards = true
                }
                boardAlreadyLoading = false
                clickHandler?.hideSavedPostSwipe()
            }

            runWithDelay(100)
            {
                loadBoard = true
            }
        } else if (userBoard.value!!.isEmpty() && currentTab == 1) {
            userBoard.postValue(ArrayList())
            showNoBoards = true
        }

    }

/*    fun hasInternet(): Boolean {
        if (!Util.checkIfHasNetwork()) {
            toastMessage.postValue(resource.getString(R.string.connectErr))
            return false
        }
        return true
    }*/

}