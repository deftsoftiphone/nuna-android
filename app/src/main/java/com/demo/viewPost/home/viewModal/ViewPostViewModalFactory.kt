package com.demo.viewPost.home.viewModal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.search.SearchRepository
import com.demo.repository.viewPost.ViewPostRepository

class ViewPostViewModalFactory(
    private val viewPostRepository: ViewPostRepository,
    private val homeRepository: HomeRepository,
    private val searchRepository: SearchRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewPostViewModal(viewPostRepository,homeRepository, searchRepository, resources) as T
    }
}
