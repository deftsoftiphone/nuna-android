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
import com.demo.repository.home.HomeRepository
import com.demo.repository.search.SearchRepository
import com.demo.search.clickListeners.FollowClickListener
import com.demo.user_profile_details.FollowersUsersRecyclerAdapter
import com.demo.user_profile_details.FollowingFollowersFragment
import com.demo.util.Prefs
import com.demo.util.plusAssign

class FollowingFollowersViewModel(
    private val homeRepository: HomeRepository,
    private val searchRepository: SearchRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    var followers = MutableLiveData<ArrayList<Activity>>(ArrayList())
    var followings = MutableLiveData<ArrayList<Activity>>(ArrayList())
    var suggested = MutableLiveData<ArrayList<Activity>>(ArrayList())

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
    private lateinit var face3: Typeface
    private lateinit var face4: Typeface
    lateinit var followersAdapter: FollowersUsersRecyclerAdapter
    lateinit var followingsAdapter: FollowersUsersRecyclerAdapter
    lateinit var suggestedAdapter: FollowersUsersRecyclerAdapter
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
        face3 = resources.getFont("fonts/nirmala.ttf")
        face4 = resources.getFont("fonts/nirmala_b.ttf")
    }

    fun setupClickHandler(clickHandler: FollowingFollowersFragment.ClickHandler) {
        this.clickHandler = clickHandler
    }


    private fun initRecyclerAdapters() {
        followersAdapter = FollowersUsersRecyclerAdapter(
            R.layout.followers_users_item,
            followersUserClickListener,
            if (Prefs.init().selectedLangId == 2) face1 else face3,
            if (Prefs.init().selectedLangId == 2) face2 else face4,
        )

        followingsAdapter = FollowersUsersRecyclerAdapter(
            R.layout.followers_users_item,
            followingUserClickListener,
            if (Prefs.init().selectedLangId == 2) face1 else face3,
            if (Prefs.init().selectedLangId == 2) face2 else face4,
        )
        suggestedAdapter = FollowersUsersRecyclerAdapter(
            R.layout.followers_users_item,
            suggestedUserClickListener,
            if (Prefs.init().selectedLangId == 2) face1 else face3,
            if (Prefs.init().selectedLangId == 2) face2 else face4,
        )
    }


    fun updateParamsAccordingToTab() {
        queryParams.tab = getCurrentTab()
        queryParams.offset = getCurrentTabListOffset()
        if (!userId.get().isNullOrEmpty()) {
            queryParams.userId = userId.get()

        }
        clearCurrentTab()
        getProfileData(0)
    }


    fun getProfileData(offset: Int) {
        queryParams.offset = offset
        showLoading.postValue(true)
        homeRepository.getProfileData(
            queryParams,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess && response.data?.users!!.isNotEmpty()) {
                    updateRecordsAccordingToTab(response)
                    showNoData.postValue(false)
                } else showNoData.postValue(true)

//                updateShowNoData(response)
                showLoading.postValue(false)
            })
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
                        } else followers.value?.showNoData()
                    1 ->
                        if (it.isNotEmpty()) {
                            showNoData.postValue(false)
                            followings += it
                            allFollowingsLoaded = false
                        } else followings.value?.showNoData()
                    else ->
                        if (it.isNotEmpty()) {
                            showNoData.postValue(false)
                            suggested += it
                            allSuggestedLoaded = false
                        } else suggested.value?.showNoData()
                }
            }
        }
    }

    private fun <T> ArrayList<T>.showNoData() {
        if (this.isEmpty()) showNoData.postValue(true)
        allSuggestedLoaded = true
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

    fun followUser(
        position: Int,
        id: String,
        isFollowed: Boolean,
        adapter: FollowersUsersRecyclerAdapter
    ) {
        searchRepository.followUser(User(userId = id),
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    adapter.apply {
                        list[position].noOfFollowers = response.data?.noOfFollowers?.toInt()
                        adapter.list[position].followedByMe = !isFollowed
                        adapter.notifyItemChanged(position)
                    }
                } else toastMessage.postValue(response.error?.message!!)
            })
    }


    suspend fun followUser(id: String): LiveData<BaseResponse> {
        return homeRepository.followUser(User(userId = id))
    }


    private fun getCurrentTab() = when (currentTab.get()) {
        0 -> resources.getString(R.string.key_followers)
        1 -> resources.getString(R.string.key_following)
        else -> resources.getString(R.string.key_suggested)
    }


    fun getCurrentTabListOffset() = when (currentTab.get()) {
        0 -> followers.value?.size
        1 -> followings.value?.size
        else -> suggested.value?.size
    }

    private fun clearCurrentTab() {
        when (currentTab.get()) {
            0 -> followers.postValue(ArrayList())
            1 -> followings.postValue(ArrayList())
            else -> suggested.postValue(ArrayList())
        }
    }
}