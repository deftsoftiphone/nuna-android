package com.demo.search

import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.search.SearchRepository
import com.demo.search.adapters.HashTagsAdapter
import com.demo.search.adapters.UsersRecyclerAdapter
import com.demo.search.adapters.VideosPostsAdapter
import com.demo.search.clickListeners.FollowClickListener
import com.demo.util.Prefs
import com.demo.util.Util
import com.demo.util.plusAssign
import com.demo.util.runWithDelay

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {
    lateinit var textWatcher: TextWatcher
    private var handler: Handler = Handler(Looper.getMainLooper() /*UI thread*/)
    private var workRunnable: Runnable? = null

    var users = MutableLiveData<ArrayList<Activity>>(ArrayList())
    var videos = MutableLiveData<ArrayList<PostAssociated>>(ArrayList())
    var hashTags = MutableLiveData<ArrayList<HashTag>>(ArrayList())
    var followUser = MutableLiveData(BaseResponse())
    var followHashTag = MutableLiveData(BaseResponse())
    var queryParams = QueryParams().apply {
        //        limit = resources.getInt(R.integer.query_param_limit_value)
        limit = 15
    }
    var currentTab = ObservableInt(0)
    var searchQuery = ObservableField("")

    private lateinit var face1: Typeface
    private lateinit var face2: Typeface
    lateinit var usersAdapter: UsersRecyclerAdapter
    lateinit var videosAdapter: VideosPostsAdapter
    lateinit var hashTagsAdapter: HashTagsAdapter
    lateinit var followUserClickListener: FollowClickListener
    lateinit var followHashTagClickListener: FollowClickListener
    private var clickHandler: SearchFragment.ClickHandler? = null
    var allUsersLoaded = false
    var allVideosLoaded = false
    var allHashTagsLoaded = false
    var isSearching = false

    init {
        initTypeFaces()
        initTextWatcher()
        followUserClickListener()
        followHashTagClickListener()
        initRecyclerAdapters()
        updateParamsAccordingToTab()
    }

    fun setupClickHandler(clickHandler: SearchFragment.ClickHandler) {
        this.clickHandler = clickHandler
    }

    private fun initTypeFaces() {
        if (Prefs.init().selectedLangId == 2) {
            face1 = resources.getFont("fonts/gotham_pro.ttf")
            face2 = resources.getFont("fonts/gothampro_medium.ttf")
        } else {
            face1 = resources.getFont("fonts/nirmala.ttf")
            face2 = resources.getFont("fonts/nirmala_b.ttf")
        }
    }

    private fun initRecyclerAdapters() {
        usersAdapter = UsersRecyclerAdapter(
            R.layout.search_users_item,
            followUserClickListener,
            face1,
            face2
        )
        hashTagsAdapter = HashTagsAdapter(
            R.layout.search_hashtag_item, followHashTagClickListener, face1, face2
        )
        videosAdapter = VideosPostsAdapter(R.layout.layout_search_video_item)
    }

    private fun initTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                workRunnable?.let { handler.removeCallbacks(it) }
                workRunnable = Runnable {
                    setupPaginationParams()
                    queryParams.offset = 0
                    clearSearchingTab()
                    getSearchResults(0)
                }
                handler.postDelayed(workRunnable!!, 500 /*delay*/)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
    }

    fun updateParamsAccordingToTab() {
        queryParams.tab = getCurrentTab()
        clearSearchingTab()
        updateQueryType()
        getSearchResults(0)
    }

    private fun updateQueryType() {
        queryParams.apply {
            searchText = if (currentTab.get() == 1) searchQuery.get() else ""
            startsWith = if (currentTab.get() != 1) searchQuery.get() else ""
        }
    }

    fun getSearchResults(offset: Int) {
        if (!isSearching) {
            if (offset == 0) {
                clearSearchingTab()
            }
            queryParams.offset = offset
            showLoading.postValue(true)
            searchRepository.getSearchResult(
                queryParams,
                onResult = { isSuccess: Boolean, response: BaseResponse ->
                    if (isSuccess) {
                        updateRecordsAccordingToTab(response)
                    } else {
                        if (!Util.checkIfHasNetwork())
                            response.error!!.message?.let { toastMessage.postValue(it) }
                    }
                    showLoading.postValue(false)
                    showNoData.postValue(getCurrentTabListOffset() < 1)
                })
        }
    }

    /*  fun showNoData() {
          when (currentTab.get()) {
              0 -> {
                  if (usersAdapter.itemCount < 1)
                      showNoData.postValue(true)
              }
              1 -> {
                  if (videosAdapter.itemCount < 1)
                      showNoData.postValue(true)
              }
              2 -> {
                  if (hashTagsAdapter.itemCount < 1)
                      showNoData.postValue(true)
              }
          }
      }
  */

    fun setupPaginationParams() {
        queryParams.tab = getCurrentTab()
        queryParams.offset = getCurrentTabListOffset()
        updateQueryType()
    }

    private fun updateRecordsAccordingToTab(response: BaseResponse) {
        response?.data?.let { res ->
            when (currentTab.get()) {
                0 -> res.users?.let {
                    if (it.isNotEmpty()) {
                        users += it
                        allUsersLoaded = false
                    } else allUsersLoaded = true
                    if (res.paging?.page?.totalCount == users.value?.size) {
                        allUsersLoaded = true
                    }
                }
                1 -> res.videoPosts?.let {
                    if (it.isNotEmpty()) {
                        videos += it
                        allVideosLoaded = false
                    } else allVideosLoaded = true
                    if (res.paging?.page?.totalCount == videos.value?.size) {
                        allVideosLoaded = true
                    }
                }
                else -> res.hashTags?.let {
                    if (it.isNotEmpty()) {
                        hashTags += it
                        allHashTagsLoaded = false
                    } else allHashTagsLoaded = true
                    if (res.paging?.page?.totalCount == hashTags.value?.size) {
                        allHashTagsLoaded = true
                    }
                }
            }
        }

        runWithDelay(200) {
            isSearching = false
        }
    }

    private fun followUserClickListener() {
        followUserClickListener = object : FollowClickListener {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followUser(position, id!!, isFollowed)
            }

            override fun openScreen(id: String, categoryId: String?) {
                clickHandler?.openUser(id)
            }
        }
    }

    fun followUser(position: Int, id: String, isFollowed: Boolean) {
        searchRepository.followUser(User(userId = id),
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    usersAdapter.apply {
                        list[position].noOfFollowers = response.data?.noOfFollowers?.toInt()
                        list[position].followedByMe = !isFollowed
                        notifyItemChanged(position)
                    }
                } else if (!Util.checkIfHasNetwork() || response.error?.status != 404)
                    response.error!!.message?.let { toastMessage.postValue(it) }
            })
    }


    private fun followHashTagClickListener() {
        followHashTagClickListener = object : FollowClickListener {
            override fun onFollowClick(position: Int, id: String?, isFollowed: Boolean) {
                followHashTag(position, id!!, isFollowed)
            }

            override fun openScreen(id: String, categoryId: String?) {
                clickHandler?.openHashTag(id, categoryId!!)
            }

        }
    }

    fun followHashTag(position: Int, id: String, isFollowed: Boolean) {
        hashTagRepository.followHashTag(FollowHashTagRequest(hashTagId = id),
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    hashTagsAdapter.apply {
                        list[position].followedByMe = !isFollowed
                        notifyItemChanged(position)
                    }
                } else response.error!!.message?.let { toastMessage.postValue(it) }

            })
    }

    fun clearSearch() {
        searchQuery.set("")
    }

    private fun getCurrentTab() = when (currentTab.get()) {
        0 -> resources.getString(R.string.search_query_users)
        1 -> resources.getString(R.string.search_query_videos)
        else -> resources.getString(R.string.search_query_hashtag)
    }

    fun getCurrentTabListOffset(): Int = when (currentTab.get()) {
        0 -> users.value?.size!!
        1 -> videos.value?.size!!
        else -> hashTags.value?.size!!
    }

    fun clearSearchingTab() {
        when (currentTab.get()) {
            0 -> users.postValue(ArrayList())
            1 -> videos.postValue(ArrayList())
            else -> hashTags.postValue(ArrayList())
        }
    }
}