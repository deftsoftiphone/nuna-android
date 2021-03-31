package com.demo.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.R
import com.demo.util.Util

open class ParentViewModel : ViewModel() {
    var showLoading = MutableLiveData<Boolean>().apply { value = false }
    var toastMessage = MutableLiveData<String>()
    var showNoData = MutableLiveData<Boolean>().apply { value = false }


}