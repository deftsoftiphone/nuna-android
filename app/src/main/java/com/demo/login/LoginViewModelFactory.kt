package com.demo.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.repository.login.LoginRepository

class LoginViewModelFactory(
    private val loginRepository: LoginRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository) as T
    }
}