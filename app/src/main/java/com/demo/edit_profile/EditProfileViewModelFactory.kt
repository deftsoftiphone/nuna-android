package com.demo.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.providers.resources.ResourcesProvider
import com.demo.repository.home.HomeRepository
import com.demo.repository.login.LoginRepository

class EditProfileViewModelFactory(
    private val homeRepository: HomeRepository

) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(homeRepository) as T
    }
}