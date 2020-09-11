package com.demo.hashtag_tab

import com.demo.model.response.baseResponse.HashTag
import com.demo.model.response.baseResponse.PostAssociated

interface HashTagPostClickListener {
    fun postClicked(hashTagId:String, post: PostAssociated, position:Int)
}