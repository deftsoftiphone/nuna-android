package com.demo.search

import android.graphics.Typeface
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.response.baseResponse.Activity
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.SearchRepository
import com.demo.repository.home.HomeRepository
import com.demo.search.adapters.UsersRecyclerAdapter
import com.demo.search.clickListeners.FollowClickListener
import com.demo.util.plusAssign

class FollowingFollowersViewModel(
    private val homeRepository: HomeRepository,
    private val searchRepository: SearchRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    var followers = MutableLiveData<ArrayList<Activity>>().apply { value = ArrayList() }
    var followings = MutableLiveData<ArrayList<Activity>>().apply { value = ArrayList() }
    var suggested = MutableLiveData<ArrayList<Activity>>().apply { value = ArrayList() }

    var followUser = MutableLiveData<BaseResponse>()
    var queryParams = QueryParams().apply {
        //        limit = resources.getInt(R.integer.query_param_limit_value)
        limit = 20
    }
    var currentTab = ObservableInt(0)
    var userId = ObservableField<String>("")
    private var clickHandler: FollowingFollowersFragment.ClickHandler? = null

    private lateinit var face1: Typeface
    private lateinit var face2: Typeface
    lateinit var followersAdapter: UsersRecyclerAdapter
    lateinit var followingsAdapter: UsersRecyclerAdapter
    lateinit var suggestedAdapter: UsersRecyclerAdapter
    lateinit var followersUserClickListener: FollowClickListener
    lateinit var followingUserClickListener: FollowClickListener
    lateinit var suggestedUserClickListener: FollowClickListener
    var allFollowersLoaded = false
    var allFollowingsLoaded = false
    var allSuggestedLoaded = false

    init {
        initTypeFaces()
        followUserClickListener()
    }

    private fun initTypeFaces() {
        face1 = resources.getFont("fonts/gotham_pro.ttf")
        face2 = resources.getFont("fonts/gothampro_medium.ttf")
    }

    fun setupClickHandler(clickHandler: FollowingFollowersFragment.ClickHandler) {
        this.clickHandler = clickHandler
    }


    private fun initRecyclerAdapters() {
        followersAdapter = UsersRecyclerAdapter(
            R.layout.search_users_item,
            followersUserClickListener,
            face1,
            face2
        )

        followingsAdapter = UsersRecyclerAdapter(
            R.layout.search_users_item,
            followingUserClickListener,
            face1,
            face2
        )
        suggestedAdapter = UsersRecyclerAdapter(
            R.layout.search_users_item,
            suggestedUserClickListener,
            face1,
            face2
        )


    }


    fun updateParamsAccordingToTab() {
        clearSearchingTab()

        queryParams.tab = getCurrentTab()
        queryParams.offset = getCurrentTabListOffset()
        if (!userId.get().isNullOrEmpty()) {
            queryParams.userId = userId.get()

        }
        getProfileData(0)

    }


    fun getProfileData(offset: Int) {
        queryParams.offset = offset
        showLoading.postValue(true)
        homeRepository.getProfileData(
            queryParams,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess  && response.data?.users!!.isNotEmpty()) {
                    updateRecordsAccordingToTab(response)
                    showNoData.postValue(false)
                } else showNoData.postValue(true)

//                updateShowNoData(response)
                showLoading.postValue(false)
            })
    }


    private fun updateShowNoData(response: BaseResponse) {
        if (response.error == null || response.statusErrorCode == 305) {
            showNoData.postValue(false)
        } else showNoData.postValue(true)
    }


    fun clearSearchingTab() {
        when (currentTab.get()) {
            0 -> followers.postValue(ArrayList())
            1 -> followings.postValue(ArrayList())
            else -> suggested.postValue(ArrayList())
        }
    }

    private fun updateRecordsAccordingToTab(response: BaseResponse) {
        response.data?.let { res ->
            res.users?.let {
                when (currentTab.get()) {
                    0 ->
                        if (it.isNotEmpty()) {
                            showNoData.postValue(false)
                            followers += it
                            allFollowersLoaded = false
                        } else {
                            if (followers.value?.size == 0) showNoData.postValue(true)
                            allFollowersLoaded = true
                        }
                    1 ->
                        if (it.isNotEmpty()) {
                            showNoData.postValue(false)
                            followings += it
                            allFollowingsLoaded = false
                        } else {
                            if (followings.value?.size == 0) showNoData.postValue(true)
                            allFollowingsLoaded = true
                        }
                    else ->
                        if (it.isNotEmpty()) {
                            showNoData.postValue(false)
                            suggested += it
                            allSuggestedLoaded = false
                        } else {
                            if (suggested.value?.size == 0) showNoData.postValue(true)
                            allSuggestedLoaded = true
                        }
                }
            }
        }
    }

    private fun followUserClickListener() {
        followersUserClickListener = object : FollowClickListener {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followUser(position, id!!, isFollowed, followersAdapter)
            }

            override fun openScreen(id: String, categoryId: String?) {
                clickHandler?.openUser(id)

            }
        }

        followingUserClickListener = object : FollowClickListener {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followUser(position, id!!, isFollowed, followingsAdapter)
            }

            override fun openScreen(id: String, categoryId: String?) {
                clickHandler?.openUser(id)

            }
        }

        suggestedUserClickListener = object : FollowClickListener {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followUser(position, id!!, isFollowed, suggestedAdapter)
            }

            override fun openScreen(id: String, categoryId: String?) {
                clickHandler?.openUser(id)

            }
        }

        initRecyclerAdapters()
    }

    fun followUser(position: Int, id: String, isFollowed: Boolean, adapter: UsersRecyclerAdapter) {
        searchRepository.followUser(User(userId = id),
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    adapter.apply {
                        list[position].noOfFollowers=response.data?.noOfFollowers?.toInt()
                        list[position].followedByMe = !isFollowed
                        notifyItemChanged(position)
                    }
                } else toastMessage.postValue(response.error?.message)
            })
    }


    suspend fun followUser(id: String): LiveData<BaseResponse> {
        return homeRepository.followUser(User(userId = id))
    }


    private fun getCurrentTab() = when (currentTab.get()) {
        0 -> resources.getString(R.string.followers_small)
        1 -> resources.getString(R.string.following_small)
        else -> resources.getString(R.string.suggested_small)
    }


    fun getCurrentTabListOffset() = when (currentTab.get()) {
        0 -> followers.value?.size
        1 -> followings.value?.size
        else -> suggested.value?.size
    }

    fun updateCurrentTabListOffset() = when (currentTab.get()) {
        0 -> queryParams.offset = followers.value?.size
        1 -> queryParams.offset = followings.value?.size
        else -> queryParams.offset = suggested.value?.size
    }


}