package com.demo.hashtag_tab

import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated

interface HashTagPostClickListener {
    fun hashTagClicked(hashTagId:String, post: PostAssociated, position:Int)
    fun postClicked(hashTagId:String, posts: ArrayList<PostAssociated>, position:Int)
}