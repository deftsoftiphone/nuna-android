package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseGetPostDetails(

	@field:SerializedName("post")
	val post: Post? = null,

	@field:SerializedName("totalComments")
	val totalComments: Int = 0,

	@field:SerializedName("commentList")
	var commentList: List<UserComment> = emptyList(),

	@field:SerializedName("relatedPost")
	val relatedPost: Any? = null
)