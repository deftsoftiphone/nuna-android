package com.demo.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.search.SearchRepository
import com.demo.repository.home.HomeRepository

class FollowingFollowersViewModelfactory(
    private val searchRepository: SearchRepository,
    private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FollowingFollowersViewModel(homeRepository,searchRepository, resources) as T
    }
}