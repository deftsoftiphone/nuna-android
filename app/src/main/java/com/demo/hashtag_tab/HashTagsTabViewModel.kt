package com.demo.hashtag_tab

import android.os.Handler
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.create_post.CategoryClickedListener
import com.demo.model.request.RequestGetCategory
import com.demo.model.request.RequestGetFollowingFollowerUsersList
import com.demo.model.request.RequestGetPostList
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.util.Prefs
import com.demo.util.Util
import com.demo.util.plusAssign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HashTagsTabViewModel(
//    controller: AsyncViewController,
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    var lastRequestGetPost = MutableLiveData<RequestGetPostList>()
    var position = MutableLiveData<Int>().apply { value = 0 }
    private val requestGetFollowingFollower =
        MutableLiveData<RequestGetFollowingFollowerUsersList>()
    private val searchHandler = Handler()
    private var lastSearchKeyword = ObservableField<String>("")

    private var requestGetCategory = MutableLiveData<RequestGetCategory>()
    private var getHashTagParams = MutableLiveData<QueryParams>()
    var categories = MutableLiveData<ArrayList<PostCategory>>(ArrayList())
    var selectedCategoryPosition = MutableLiveData<Int>().apply { value = 0 }
    var selectedCategory = MutableLiveData<PostCategory>()

    private var getCategoriesParams = QueryParams()
    var hashTagOverview = MutableLiveData<ArrayList<HashTag>>(ArrayList())
    var hashTagBanner = MutableLiveData<ArrayList<HashTagImagesItem>>(ArrayList())
    var loadedAllPages = MutableLiveData(false)
    var hastTagBannerParams = QueryParams()
    var allBannerLoaded = false
    var isLoading = MutableLiveData(false)
    var hashTagOverViewRecyclerAdapter: HashTagTabRecyclerAdapter? = null

    init {
        requestGetCategory.value = RequestGetCategory()
        getHashTagParams.value = QueryParams()
        lastRequestGetPost.value = RequestGetPostList()
        requestGetFollowingFollower.value = RequestGetFollowingFollowerUsersList()
        categories.value = ArrayList()
        initCategoryParams()
        initHashTagParams()
        hastTagBannerParams.apply {
            offset = 0
            limit = resources.getInt(R.integer.query_param_limit_value)
        }
    }

    private fun initCategoryParams() {
        getCategoriesParams = QueryParams().apply {
            offset = 0
            limit = 100
            languageId = Prefs.init().selectedLang._id!!
        }

    }

    private fun initHashTagParams() {
        getHashTagParams.value = QueryParams()
        getHashTagParams.value?.apply {
            postOffset = resources.getInt(R.integer.default_offset)
            postLimit = resources.getInt(R.integer.default_hashtag_post_offset)
            hashTagOffset = resources.getInt(R.integer.default_offset)
            hashTagLimit = resources.getInt(R.integer.query_param_limit_value)
            categoryId = selectedCategory.value?.id
            languageId = Prefs.init().selectedLang._id!!
        }
    }

    fun updateCategories(categories: ArrayList<PostCategory>) {
        this.categories.value!!.clear()
        this.categories.value!!.addAll(categories)
    }

    /*suspend fun getCategories(offSet: Int): LiveData<BaseResponse> {
        getCategoriesParams.value?.offset = offSet
        return hashTagRepository.getCategories(getCategoriesParams.value!!)
    }*/


    fun getCategories(offset: Int) {
        showLoading.postValue(true)
        getCategoriesParams.offset = offset
        if (offset == 0) categories.postValue(ArrayList())

        viewModelScope.launch {
            hashTagRepository.getCategories(getCategoriesParams) { isSuccess, response ->
                showLoading.postValue(false)
                GlobalScope.launch(Dispatchers.Main) {
                    if (isSuccess) {
                        if (!response.data?.postCategoryList.isNullOrEmpty()) {
                            categories += response.data?.postCategoryList!!
                        }
                    } else response.error?.message?.let { toastMessage.postValue(it) }
                }
            }
        }
    }


    fun categoryClickListener(): CategoryClickedListener {
        return object : CategoryClickedListener {
            override fun selectedCategory(value: PostCategory) {
                loadedAllPages.postValue(false)
                selectedCategory.value = value
                getHashTagParams.value?.categoryId =
                    if (selectedCategory.value?.id == null) null else selectedCategory.value?.id!!
                getHashTagOverview(0)
            }
        }
    }

    fun clearOverview() {
        hashTagOverview.value?.clear()
        hashTagOverview.postValue(ArrayList())
    }

    fun getHashTagOverview(offSet: Int) {
        isLoading.postValue(true)
        if (offSet == 0) clearOverview()
        getHashTagParams.value?.hashTagOffset = offSet
        showLoading.postValue(true)
        hashTagRepository.getHashTagOverview(
            getHashTagParams.value!!,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    showNoData.postValue(false)
                    response.data?.hashTags?.let {
                        if (it.isNotEmpty()) {
                            hashTagOverview += it
                            val pos = hashTagOverview.value?.lastIndex
                            hashTagOverViewRecyclerAdapter?.addNewItems(it)
                        }

                        if (it.size < 10 || it.isEmpty())
                            loadedAllPages.postValue(true)
                        else loadedAllPages.postValue(false)

                        if (hashTagOverview.value?.isEmpty()!!)
                            showNoData.postValue(true)
                    }
                } else {
                    showNoData.postValue(true)
                    response.error?.message?.let { toastMessage.postValue(it) }
                }
                showLoading.postValue(false)
            })
    }

    fun getHashTagBanner(offset: Int) {
        hastTagBannerParams.offset = offset
        showLoading.postValue(true)
        hashTagRepository.getHashTagBanner(hastTagBannerParams) { isSuccess, response ->
            if (isSuccess) {
                response.data?.hashTagImages?.let {
                    hashTagBanner += it

                    if (it.size < resources.getInt(R.integer.query_param_limit_value))
                        allBannerLoaded = true
                }
            } else if (!Util.checkIfHasNetwork())
                response.error?.message?.let { toastMessage.postValue(it) }
        }
    }
}