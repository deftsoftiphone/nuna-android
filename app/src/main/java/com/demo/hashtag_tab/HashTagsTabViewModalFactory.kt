package com.demo.hashtag_tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.hashtag.HashTagRepository

class HashTagsTabViewModalFactory(
    private val hashTagRepository: HashTagRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HashTagsTabViewModel(hashTagRepository,resources) as T
    }
}
