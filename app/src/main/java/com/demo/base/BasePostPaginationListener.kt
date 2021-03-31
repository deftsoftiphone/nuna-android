package com.demo.base

import com.demo.model.response.baseResponse.PostAssociated

interface BasePostPaginationListener {
    fun onDataLoad(posts: ArrayList<PostAssociated>)
}