package com.demo.viewPost.clickhandler

import com.demo.model.response.baseResponse.PostAssociated

interface PostUpdateListener {

    fun onPostUpdate(data: PostAssociated,position : Int)
    fun onFollowUpdate(userId: String)

}