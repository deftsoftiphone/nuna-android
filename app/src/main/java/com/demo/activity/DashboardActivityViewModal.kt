package com.demo.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.base.ParentViewModel
import com.demo.model.response.baseResponse.PostAssociated
import com.demo.model.response.baseResponse.PostDetailRequest
import com.demo.repository.home.HomeRepository
import com.demo.repository.viewPost.ViewPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DashboardActivityViewModal(
    private val homeRepository: HomeRepository,
    private val viewPostRepository: ViewPostRepository,
) : ParentViewModel() {

    var notificationCount = MutableLiveData(0)
    var postFromNotification = MutableLiveData(PostAssociated())

    fun getNotificationCount() {
        homeRepository.getNotificationCount { isSuccess, response ->
            if (isSuccess) {
                notificationCount.postValue(response.data?.tolalNumberOfNotifications)
            } else notificationCount.postValue(0)
        }
    }

    fun readNotification(id: String) {
        homeRepository.readNotification(id) { isSuccess, response ->
            if (isSuccess) {
                getNotificationCount()
            }
        }
    }

    fun getPostDetail(postId: String) {
        showLoading.postValue(true)
        viewModelScope.launch {
            viewPostRepository.getPostDetail(
                PostDetailRequest(postId),
                onResult = { isSuccess, response ->
                    viewModelScope.launch(Dispatchers.Main) {
                        showLoading.postValue(false)
                        if (isSuccess) {
                            response.data?.postDetail?.let {
                                postFromNotification.postValue(it)
                            }
                        } else toastMessage.postValue(response.error?.message)
                    }
                })
        }
    }
}