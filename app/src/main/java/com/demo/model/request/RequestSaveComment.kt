package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName


data class RequestSaveComment(

    @field:SerializedName("user")
	var user: User? = User(Prefs.init().loginData!!.userId),

    @field:SerializedName("commentId")
	var commentId: Int? = 0,

    @field:SerializedName("parentCommentId")
	var parentCommentId: Int? = 0,

    @field:SerializedName("postId")
	var postId: Int? = null,

    @field:SerializedName("commentText")
	var commentText: String? = "",

    @field:SerializedName("url")
	var isUrl: Boolean? = false
)