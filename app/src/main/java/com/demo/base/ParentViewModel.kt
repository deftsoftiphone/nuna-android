package com.demo.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class ParentViewModel : ViewModel() {
    var showLoading = MutableLiveData<Boolean>().apply { value = false }
    var toastMessage = MutableLiveData<String>()
    var showNoData = MutableLiveData<Boolean>().apply { value = false }

}