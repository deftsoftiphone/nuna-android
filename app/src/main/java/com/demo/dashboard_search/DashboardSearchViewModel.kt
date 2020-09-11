package com.demo.dashboard_search

import android.location.Location
import android.os.Handler
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.R
import com.demo.create_post.CategoryClickedListener
import com.demo.model.request.RequestGetCategory
import com.demo.model.request.RequestGetPostList
import com.demo.model.response.*
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.post.PostRepository
import com.demo.util.Prefs

class DashboardSearchViewModel(
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository,
    private val resources: ResourcesProvider
) : ViewModel() {
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


    var followingParams = QueryParams()
    var popularParams = QueryParams()
    var params = MutableLiveData<QueryParams>()
    var categories = ArrayList<Category>()
    var selectedCategory = Category()

    init {
        requestGetCategory.value = RequestGetCategory()
        lastRequestGetPost.value = RequestGetPostList()
        params.value = QueryParams()
    }


    suspend fun getFolllowing(offset:Int): LiveData<BaseResponse> {
        followingParams.offset = offset
        followingParams.limit = 10
        followingParams.token= Prefs.init().deviceToken
        return homeRepository.getDiscoverFollowing(followingParams)
    }

    suspend fun getPopular(offset: Int,categoryId:String?): LiveData<BaseResponse> {
        popularParams.offset = offset
        popularParams.limit = 10
        popularParams.categoryId=categoryId
        return homeRepository.getDiscoverPopular(popularParams)
    }


    suspend fun getCategories(): LiveData<BaseResponse> {
        params.value?.offset = categories.size
        return postRepository.getCategories(params.value!!)
    }

    fun categoryClickListener(): CategoryClickedListener {
        return object : CategoryClickedListener {
            override fun selectedCategory(value: Category) {
                selectedCategory = value

            }
        }
    }
    fun updateCategories(categories: ArrayList<Category>) {
        this.categories.clear()
        if (this.categories.isEmpty())
            categories.add(Category(categoryName = resources.getString(R.string.all_categories)))

        this.categories.addAll(categories)
    }
    /*public val searchRunnable = Runnable {
        initiateSearchQuery()
    }*/

    /*
    * attached directly with layout via binding
    * waits for 400 millis before executing search api
    * cancels previous pending api calls if new search key appears
    * */
  /*  fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (lastSearchKeyword.get()?.isEmpty() == true && s.isEmpty()) return
        searchHandler.removeCallbacksAndMessages(null)
        lastSearchKeyword.set(s.toString())
        //searchHandler.postDelayed(searchRunnable,400)
    }*/


    /*
    * calls api to get document list based on text search or date search
    * */


    /*
    * calls api with same params as last , but with next page queryresponseLikeUnLikePost
    * result should be added in adapter
    * */
    fun loadNextPage() {
        pagerProgress.value = true
        isPageLoading = true
        lastRequestGetPost.value!!.pageNumber++
        Log.d("PAGINGGG", lastRequestGetPost.value.toString())
//        callGetPostList(responseGetPostList_PAGING, false)
    }
/*
    fun callLikeUnlikePostApi(postId: Int, postLiked: Boolean) {
//        val request = RequestLikeUnLikePost(postId, postLiked)//        val request = RequestLikeUnLikePost(postId, postLiked)

//        baseRepo.restClient.callApi(ApiRegister.LIKEUNLIKEPOST, request, responseLikeUnLikePost)
    }*/
}