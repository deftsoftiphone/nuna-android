package com.demo.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.viewPost.ViewPostRepository

class DashboardActivityViewModalFactory(
    private val homeRepository: HomeRepository,
    private val viewPostRepository: ViewPostRepository,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DashboardActivityViewModal(homeRepository, viewPostRepository) as T
    }
}