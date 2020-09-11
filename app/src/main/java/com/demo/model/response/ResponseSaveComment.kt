package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseSaveComment(

	@field:SerializedName("commentId")
	val commentId: Int? = null,

	@field:SerializedName("parentCommentId")
	val parentCommentId: Int? = null,

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("commentText")
	val commentText: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("replyCount")
	val replyCount: Int? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("replyCommentList")
	val replyCommentList: Any? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("url")
	val url: Boolean? = null
)