package com.demo.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.login.LoginRepository

class SettingsViewModelFactory(
    private val loginRepository: LoginRepository,
     private val homeRepository: HomeRepository,
    private val resources: ResourcesProvider
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsViewModel(loginRepository, homeRepository, resources) as T
    }
}