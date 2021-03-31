package com.demo.viewPost.clickhandler

import com.demo.model.response.baseResponse.PostAssociated

interface ShareClickHandler {
    fun onWhatsappShareClick(model: PostAssociated, adapterPosition: Int)
    fun onMessageShareClick(model: PostAssociated, adapterPosition: Int)
    fun onStatusShareClick(model: PostAssociated, adapterPosition: Int)
    fun onInstagramShareClick(model: PostAssociated, adapterPosition: Int)
    fun onINSShareClick(model: PostAssociated, adapterPosition: Int)
    fun onFacebookShareClick(model: PostAssociated, adapterPosition: Int)
    fun deletePost(model: PostAssociated, adapterPosition: Int)
}