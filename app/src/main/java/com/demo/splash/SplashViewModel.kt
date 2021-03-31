package com.demo.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel : ViewModel(){
    var proceedAhead = MutableLiveData<Boolean>()
}