package com.demo.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.login.LoginRepository

class ProfileViewModelFactory(
    private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider

) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(homeRepository,resources) as T
    }
}