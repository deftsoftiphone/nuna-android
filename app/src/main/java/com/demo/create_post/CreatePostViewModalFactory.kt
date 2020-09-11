package com.demo.create_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.post.PostRepository
import com.demo.webservice.APIService

class CreatePostViewModalFactory(
    private val postRepository: PostRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CreatePostViewModel(postRepository,resources) as T
    }
}
