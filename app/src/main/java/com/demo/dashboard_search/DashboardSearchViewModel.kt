package com.demo.dashboard_search

import android.location.Location
import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.base.BasePostPaginationListener
import com.demo.base.ParentViewModel
import com.demo.create_post.CategoryClickedListener
import com.demo.model.request.RequestGetCategory
import com.demo.model.request.RequestGetPostList
import com.demo.model.response.MasterResponse
import com.demo.model.response.Post
import com.demo.model.response.ResponseLikeUnLikePost
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.PostCategory
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.post.PostRepository
import com.demo.util.Prefs
import com.demo.util.plusAssign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardSearchViewModel(
        private val homeRepository: HomeRepository,
        private val postRepository: PostRepository,
        private val resources: ResourcesProvider
) : ParentViewModel() {
    var position: Int = 0
    val responseGetPostList_NEW = MutableLiveData<MasterResponse<List<Post>>>()
    val responseGetPostList_PAGING = MutableLiveData<MasterResponse<List<Post>>>()
    var lastRequestGetPost = MutableLiveData<RequestGetPostList>()
    var latestLocation: Location? = null
    val responseLikeUnLikePost = MutableLiveData<MasterResponse<ResponseLikeUnLikePost>>()
    val requestGetCategory = MutableLiveData<RequestGetCategory>()
    val searchHandler = Handler()
    var lastSearchKeyword = ObservableField<String>("")
    val pagerProgress = MutableLiveData<Boolean>(false)
    var isPageLoading = false
    var loadedAllPages = false
    var followingPosts = mutableListOf<PostAssociated>()
    var popularPosts = mutableListOf<PostAssociated>()
    var selectedCategoryPosition = MutableLiveData<Int>().apply { value = 0 }
    var currentTab = 0
    var postPaginationListener: BasePostPaginationListener? = null

    var followingParams = QueryParams()
    var popularParams = QueryParams()
    var categoriesParams = QueryParams()
    var categories = MutableLiveData<ArrayList<PostCategory>>()
    var selectedCategory = PostCategory()
    var categoriesLoaded = false

    init {
        requestGetCategory.value = RequestGetCategory()
        lastRequestGetPost.value = RequestGetPostList()
        categoriesParams = QueryParams().apply {
            offset = 0
            limit = 100
        }
    }

    suspend fun getFollowing(offset: Int): LiveData<BaseResponse> {
        followingParams.offset = offset
        followingParams.limit = 10
        followingParams.token = Prefs.init().deviceToken
        val item = homeRepository.getDiscoverFollowing(followingParams)
        item.value?.data?.followingPosts?.let {
            postPaginationListener?.onDataLoad(it)
        }
        return item
    }

    suspend fun getPopular(
            offset: Int,
            categoryId: String?,
            useRecombee: Boolean
    ): LiveData<BaseResponse> {
        popularParams.offset = offset
        popularParams.limit = 10
        popularParams.categoryId = categoryId
        popularParams.useRecombee = useRecombee
        val item = homeRepository.getDiscoverPopular(popularParams)
        item.value?.data?.popularPosts?.let {
            postPaginationListener?.onDataLoad(it)
        }
        return item
    }

    /*suspend fun getCategories(offset: Int): LiveData<BaseResponse> {
        categoriesParams.offset = offset
        categoriesParams.limit= 100
        categoriesParams.type = resources.getString(R.string.post_text)
        return postRepository.getCategories(categoriesParams)
    }*/

    fun getCategories(offset: Int) {
        showLoading.postValue(true)
        categoriesParams.offset = offset
        if (offset == 0) {
            categories.postValue(ArrayList())
            categoriesLoaded = false
        }

        viewModelScope.launch {
            homeRepository.getCategories(categoriesParams) { isSuccess, response ->
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading.postValue(false)
                    if (isSuccess) {
                        response.data?.postCategoryList?.let {
                            if (it.isNotEmpty()) {
                                categories += response.data?.postCategoryList!!
                            }
                            if (it.size < 15)
                                categoriesLoaded = true
                        }
                    } else if (response.error?.status != 404)
                        response.error?.message?.let { toastMessage.postValue(it) }
                }
            }
        }
    }

    fun categoryClickListener(): CategoryClickedListener {
        return object : CategoryClickedListener {
            override fun selectedCategory(value: PostCategory) {
                selectedCategory = value
            }
        }
    }

    fun updateCategories(categories: ArrayList<PostCategory>) {
        this.categories.postValue(ArrayList())
        this.categories.value?.addAll(categories)
    }

    fun loadNextPage() {
        pagerProgress.value = true
        isPageLoading = true
        lastRequestGetPost.value!!.pageNumber++
//        callGetPostList(responseGetPostList_PAGING, false)
    }
}