package com.demo.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.search.SearchRepository
import com.demo.repository.home.HomeRepository

class SearchViewModelFactory(
    private val searchRepository: SearchRepository,
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchViewModel(searchRepository,hashTagRepository, resources) as T
    }
}