package com.demo.viewPost.clickhandler

import com.demo.model.response.baseResponse.Comment
import com.demo.model.response.baseResponse.PostAssociated

interface ViewPostClickHandler {
    fun onLikeClick(model: PostAssociated,position: Int)
    fun onCommentClick(model: PostAssociated,position: Int)
    fun onUserFollowClick(model: PostAssociated,position: Int)
    fun onUnLikeClick(model: PostAssociated,position: Int)
    fun onSaveClick(model: PostAssociated,position: Int)
    fun onRemoveSaveClick(model: PostAssociated,position: Int)
    fun onShareClick(model: PostAssociated,position: Int)
    fun onUserClick(id: String)
    fun onReportClick(model: PostAssociated,position: Int)
    fun onCommentLikeClick(comment: Comment, position:Int)
    fun onDeleteClick(model: PostAssociated,position: Int)
}