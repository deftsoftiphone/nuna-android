package com.demo.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository

class NotificationsViewModelFactory(
    private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider

) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NotificationsViewModel(homeRepository,resources) as T
    }
}