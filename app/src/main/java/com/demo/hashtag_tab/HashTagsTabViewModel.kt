package com.demo.hashtag_tab

import android.os.Handler
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.create_post.CategoryClickedListener
import com.demo.model.request.RequestGetCategory
import com.demo.model.request.RequestGetFollowingFollowerUsersList
import com.demo.model.request.RequestGetPostList
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.Category
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.util.Prefs

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
    var categories = MutableLiveData<ArrayList<Category>>()
    var selectedCategoryPosition = MutableLiveData<Int>().apply { value = 0 }
    var selectedCategory = MutableLiveData<Category>().apply {
        value = Category(
            categoryName = resources.getString(R.string.all_categories),
            isSelected = true
        )
    }
    private var getCategoriesParams = MutableLiveData<QueryParams>()
    var hashTagOverview = MutableLiveData<ArrayList<HashTag>>().apply { value = ArrayList() }
    var loadedAllPages = ObservableBoolean(false)

    init {
        requestGetCategory.value = RequestGetCategory()
        getHashTagParams.value = QueryParams()
        lastRequestGetPost.value = RequestGetPostList()
        requestGetFollowingFollower.value = RequestGetFollowingFollowerUsersList()
        categories.value = ArrayList()
        initCategoryParams()
        initHashTagParams()
    }

    private fun initCategoryParams() {
        getCategoriesParams.value = QueryParams()
        getCategoriesParams.value?.apply {
            offset = 0
            limit = resources.getInt(R.integer.query_param_limit_value)
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

    fun updateCategories(categories: ArrayList<Category>) {
        if (this.categories.value!!.isEmpty())
            categories.add(selectedCategory.value!!)
        this.categories.value?.addAll(categories)
    }

    suspend fun getCategories(): LiveData<BaseResponse> {
        getCategoriesParams.value?.offset = categories.value?.size
        return hashTagRepository.getCategories(getCategoriesParams.value!!)
    }

    fun categoryClickListener(): CategoryClickedListener {
        return object : CategoryClickedListener {
            override fun selectedCategory(value: Category) {
                selectedCategory.value = value
                getHashTagParams.value?.categoryId =
                    if (selectedCategory.value?.id == null) null else selectedCategory.value?.id!!
                clearOverview()
                getHashTagOverview(0)
            }
        }
    }

    fun clearOverview() {
        hashTagOverview.value?.clear()
        hashTagOverview.postValue(ArrayList())
    }

    fun getHashTagOverview(offSet: Int) {
        loadedAllPages.set(false)
        getHashTagParams.value?.hashTagOffset = offSet
        showLoading.postValue(true)
        hashTagRepository.getHashTagOverview(
            getHashTagParams.value!!,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                if (isSuccess) {
                    showNoData.postValue(false)
                    response.data?.hashTags?.let {
                        if (it.isNotEmpty()) {
                            updateOverviews(it)
                            if (it.size < 10)
                                loadedAllPages.set(true)
                        } else loadedAllPages.set(true)
                    }
                } else {
                    showNoData.postValue(true)
                    toastMessage.postValue(response.error?.message)
                }
                showLoading.postValue(false)
            })
    }

    private fun updateOverviews(hashTags: List<HashTag>) {
        hashTagOverview.postValue(hashTagOverview.apply { value?.plusAssign(hashTags) }.value)
    }
}