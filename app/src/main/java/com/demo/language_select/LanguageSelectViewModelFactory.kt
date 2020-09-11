package com.demo.language_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.repository.login.LoginRepository

class LanguageSelectViewModelFactory(
    private val loginRepository: LoginRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LanguageSelectViewModel(loginRepository) as T
    }
}