package com.demo.create_post

import android.location.Location
import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.request.RequestSavePost
import com.demo.model.response.GetCategory
import com.demo.model.response.MasterResponse
import com.demo.model.response.ResponseGetPostDetails
import com.demo.model.response.ResponseSavePost
import com.demo.model.response.baseResponse.*
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.post.PostRepository
import com.demo.util.plusAssign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class CreatePostViewModel(
//    controller: AsyncViewController,
    private val postRepository: PostRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {

    var searchQuery: String = ""
    var startingIndex: Int = 0
    var hashTags = mutableListOf<HashTag>()
    var searchTags = mutableListOf<HashTag>()
    var categories = MutableLiveData<ArrayList<PostCategory>>()
    var hashtags = MutableLiveData<ArrayList<HashTag>>(ArrayList())
    var searchHashtags = MutableLiveData<ArrayList<HashTag>>(ArrayList())
    var clickListener: CreatePostRecyclerItemClickListener? = null
    var description = ObservableField<String>()
    var selectedCategory = ObservableField<PostCategory>()
    val languageId = ObservableField<String>()
    private val errTitle = ObservableField<String>()
    var isEditAllowed = MutableLiveData<Boolean>(true)

    val responseGetPostDetails = MutableLiveData<MasterResponse<ResponseGetPostDetails>>()

    var latestLocation: Location? = null
    val requestSavePost = ObservableField<RequestSavePost>()
    var params = QueryParams()
    var categoriesParams = QueryParams()
    val responseGetCategory = MutableLiveData<MasterResponse<List<GetCategory>>>()
    val responseSavePost = MutableLiveData<MasterResponse<ResponseSavePost>>()
    var allCategoriesLoaded = false
    var addPostRequest = ObservableField(AddPostRequest())

    var searchHashTagParams = QueryParams()
    var hashTagParams = QueryParams()
    var allSearchedHashTagsLoaded = false
    var allHashTagsLoaded = false

    var categoriesAdaptor: CreatePostCategoryRecyclerAdapter? = null
    var hashTagsAdaptor: CreatePostHashTagRecyclerAdapter? = null
    var searchHashTagsAdaptor: CreatePostSearchHashtagRecyclerAdapter? = null

    init {
        description.set("")
        requestSavePost.set(RequestSavePost())
        params.offset = 0
        params.limit = 100
        params.newCategory = resources.getString(R.string.key_true)

        categoriesParams.apply {
            offset = 0
            limit = 20
        }
        searchHashTagParams.apply {
            offset = 0
            limit = 20
            newCategory = resources.getString(R.string.key_true)
        }
        hashTagParams.apply {
            offset = 0
            limit = 20
            newCategory = resources.getString(R.string.key_true)
        }
    }

    fun setupRecyclerAdapter() {
        categoriesAdaptor =
            CreatePostCategoryRecyclerAdapter(R.layout.layout_create_post_category, clickListener!!)

        hashTagsAdaptor =
            CreatePostHashTagRecyclerAdapter(R.layout.layout_create_post_hashtag, clickListener!!)

        searchHashTagsAdaptor =
            CreatePostSearchHashtagRecyclerAdapter(
                R.layout.layout_create_post_hashtag,
                clickListener!!
            )
    }

    fun isSavePostValid(): Boolean {
        return TextUtils.isEmpty(selectedCategory.get()?.toString())
    }

    fun getSearchQuery(s: String): String {
        var query = ""
        if (s.contains("#")) {
            val lastHash = s.lastIndexOf('#')
            if (s.lastIndexOf(' ') < lastHash) {
                val endIndex = if (lastHash == s.length - 1) lastHash else s.length
                query = "#${s.substring(lastHash, endIndex)}"
            }/*else {
                Log.e("startIndex",start.toString())
                if(start<= s.length){
                    var a=s.substring(0,start)
                    Log.e("***((","@@@$a")
                    var substring =a.substring(a.lastIndexOf("#"))
                    Log.e("))))","%%%${substring}")

                    if(substring.startsWith("#") && !substring.contains("\n")){
                        query=substring
                    }
                }

            }*/


        }
//        println("startingIndex = ${startingIndex}")
//        if (s.contains("#")) {
//            var endIndex = s.nextIndexOf(startingIndex, " ")
//            println("endIndex1 = ${endIndex}")
//            endIndex = if (endIndex == startingIndex) s.length else endIndex
//            println("endIndex2 = ${endIndex}")
//
//            if (endIndex < startingIndex) {
//                query = "#${s.substring(startingIndex, endIndex)}"
//            }
//        }

        return query
    }

    suspend fun getHashTags(): LiveData<BaseResponse> {
        params.offset = hashTags.size
        return postRepository.getHashTags(params)
    }

    suspend fun searchHashTag(): LiveData<BaseResponse> {
        params.startsWith = searchQuery
        return postRepository.getHashTags(params)
    }

    suspend fun createPost(): LiveData<BaseResponse> {
        return postRepository.createPost(requestSavePost.get()!!)
    }

    suspend fun createPostV2(): LiveData<BaseResponse> {
        return postRepository.createPostV2(addPostRequest.get()!!)
    }


    fun clearSearch() {
        searchQuery = ""
        searchTags.clear()
    }

    fun sortCategories() {
        categories.value?.sortByDescending { it.postAssociated?.size }
    }

    fun getCategories(offset: Int) {
        showLoading.postValue(true)
        categoriesParams.offset = offset
        if (offset == 0) {
            categories.postValue(ArrayList())
            allCategoriesLoaded = false
        }
        viewModelScope.launch {
            postRepository.getCategories(categoriesParams) { isSuccess, response ->
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading.postValue(false)
                    if (isSuccess) {
                        response.data?.postCategoryList?.let {
                            categories += response.data?.postCategoryList!!
                            categoriesAdaptor?.setNewItems(categories.value!!)
                            if (it.size < 20)
                                allCategoriesLoaded = true
                        }
                    } else {
                        response.error?.message?.let { toastMessage.postValue(it) }
                        allCategoriesLoaded = true
                    }
                }
            }
        }
    }

    fun getHashTags(offset: Int) {
        showLoading.postValue(true)
        hashTagParams.offset = offset
        if (offset == 0) {
            hashtags.postValue(ArrayList())
            allHashTagsLoaded = false
        }
        viewModelScope.launch {
            postRepository.getHashTag(hashTagParams) { isSuccess, response ->
                GlobalScope.launch(Dispatchers.Main) {
                    showLoading.postValue(false)
                    if (isSuccess) {
                        response.data?.hashTags?.let {
                            hashtags += response.data?.hashTags!!
                            hashTagsAdaptor?.setNewItems(hashtags.value!!)
                            if (it.size < 20)
                                allHashTagsLoaded = true
                        }
                    } else {
                        response.error?.message?.let { toastMessage.postValue(it) }
                        allHashTagsLoaded = true
                    }
                }
            }
        }
    }

    fun searchHashTags(offset: Int) {
//        showLoading.postValue(true)
        searchHashTagParams.offset = offset
        searchHashTagParams.startsWith = searchQuery
        if (offset == 0) {
            searchHashtags.postValue(ArrayList())
            allSearchedHashTagsLoaded = false
        }
        viewModelScope.launch {
            postRepository.getHashTag(searchHashTagParams) { isSuccess, response ->
                GlobalScope.launch(Dispatchers.Main) {
//                    showLoading.postValue(false)
                    if (isSuccess) {
                        response.data?.hashTags?.let {
                            searchHashtags += response.data?.hashTags!!
                            searchHashTagsAdaptor?.setNewItems(searchHashtags.value!!)

                            if (it.size < 20)
                                allSearchedHashTagsLoaded = true
                        }
                    } else {
                        if (response.data == null) {
                            val regex =
                                Pattern.compile("[^\u0A80-\u0AFF\u0980-\u09FF\u0C00-\u0C7F\u0B80-\u0BFF\u0C80-\u0CFF\u0900-\u097Fa-zA-Z0-9#]");

                            searchHashtags.value?.clear()
                            if (!regex.matcher(searchQuery).find()) {
                                searchHashtags.postValue(ArrayList<HashTag>().apply {
                                    add(HashTag(tagName = searchQuery))
                                })
                            }
                            searchHashTagsAdaptor?.clearData()
                            searchHashTagsAdaptor?.setNewItems(searchHashtags.value!!)
                        }
                        response.error?.message?.let { toastMessage.postValue(it) }
                        allSearchedHashTagsLoaded = true
                    }
                }
            }
        }
    }
}
