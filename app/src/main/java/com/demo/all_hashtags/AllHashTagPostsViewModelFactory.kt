package com.demo.all_hashtags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository
import com.demo.repository.hashtag.SearchRepository

class AllHashTagPostsViewModelFactory(
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllHashTagPostsViewModel(hashTagRepository, resources) as T
    }
}