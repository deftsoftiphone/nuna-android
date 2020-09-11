package com.demo.all_hashtags

import androidx.lifecycle.MutableLiveData
import com.demo.R
import com.demo.base.ParentViewModel
import com.demo.model.request.FollowHashTagRequest
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.QueryParams
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.hashtag.SearchRepository
import com.demo.util.plusAssign

class AllHashTagPostsViewModel(
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) : ParentViewModel() {
    var hashTag = MutableLiveData<HashTag>().apply { value = HashTag() }
    var hashTagPosts = MutableLiveData<ArrayList<PostAssociated>>().apply { value = ArrayList() }
    private var followHashTagRequest =
        MutableLiveData<FollowHashTagRequest>().apply { value = FollowHashTagRequest() }
    private var unFollowHashTagRequest =
        MutableLiveData<FollowHashTagRequest>().apply { value = FollowHashTagRequest() }
    private var getPostParams = MutableLiveData<QueryParams>().apply { value = QueryParams() }
     var isFollowed=false


    init {
        initPostParams()
    }

    fun updateIds(hashTagId: String, categoryId: String?) {
        this.hashTag.value?.id = hashTagId
        followHashTagRequest.value!!.hashTagId = hashTagId
        getPostParams.value?.categoryId = categoryId
    }

    private fun initPostParams() {
        getPostParams.value?.apply {
            offset = 0
            limit = resources.getInt(R.integer.query_param_limit_value)
        }
    }

    fun getPosts() {
        getPostParams.value?.offset = hashTagPosts.value?.size
        showLoading.postValue(true)
        hashTagRepository.getAllPostOfAHashTag(
            hashTag.value?.id!!,
            getPostParams.value!!,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                showLoading.postValue(false)
                if (isSuccess) {
                    response.data?.hashTag.let {
                        this.hashTag.value = it
                        hashTagPosts += it?.postAssociated!!
                    }
                } else toastMessage.value = response.error?.message
            })
    }

    fun followHashTag() {
        followHashTagRequest.value?.hashTagId = hashTag.value?.id
        showLoading.value = true
        hashTagRepository.followHashTag(followHashTagRequest.value!!,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                showLoading.value = false
                if (isSuccess) {

                    hashTag.postValue(hashTag.apply { value?.followedByMe = !isFollowed }.value)
//                    toastMessage.postValue(response.message)
                } else toastMessage.value = response.error?.message
            })
    }

    fun unFollowHashTag() {
        unFollowHashTagRequest.value?.hashTagId = hashTag.value?.id
        showLoading.value = true
        hashTagRepository.followHashTag(unFollowHashTagRequest.value!!,
            onResult = { isSuccess: Boolean, response: BaseResponse ->
                showLoading.value = false
                if (isSuccess) {
                    hashTag.postValue(hashTag.apply { value?.followedByMe = false }.value)
//                    toastMessage.postValue(response.message)
                } else toastMessage.value = response.error?.message
            })
    }
}