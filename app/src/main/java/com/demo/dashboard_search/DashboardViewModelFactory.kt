package com.demo.dashboard_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.post.PostRepository

class DashboardViewModelFactory(
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository,
    private val resources: ResourcesProvider

) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardSearchViewModel(homeRepository,postRepository,resources) as T
    }
}