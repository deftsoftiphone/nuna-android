package com.demo.activity

import com.demo.base.AsyncViewController
import com.demo.base.BaseViewModel
import java.util.*

class PhotoViewViewModel(controller: AsyncViewController) : BaseViewModel(controller) {

    var photos: ArrayList<String> = ArrayList()
    var targetPosition = 0
    var isDataTypeUrl = false

}