package com.demo.muebert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.muebert.repository.MuebertRepository
import com.demo.providers.resources.ResourcesProvider

class AwesomeAudioContentFactory(
    private val muebertRepository: MuebertRepository,
    private val resources: ResourcesProvider
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AwesomeAudioContentViewModel(muebertRepository, resources) as T
    }
}