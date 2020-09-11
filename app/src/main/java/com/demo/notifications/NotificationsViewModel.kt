package com.demo.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.demo.model.response.baseResponse.Activity
import com.demo.model.response.baseResponse.BaseResponse
import com.demo.model.response.baseResponse.QueryParams
import com.demo.model.response.baseResponse.User
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository

class NotificationsViewModel(private val homeRepository: HomeRepository, private val resourcesProvider: ResourcesProvider) : ViewModel() {


    var queryParams:QueryParams

    var activities = mutableListOf<Activity>()
    var suggestions = mutableListOf<Activity>()


    init {
        queryParams= QueryParams()
    }

    suspend fun getNotifications(tab:String): LiveData<BaseResponse> {
        queryParams.tab=tab
        queryParams.offset=0
        queryParams.limit=10
        return homeRepository.getNotifications(queryParams)
    }
    suspend fun followUser(user : User): LiveData<BaseResponse> {
        return homeRepository.followUser(user)
    }


}

