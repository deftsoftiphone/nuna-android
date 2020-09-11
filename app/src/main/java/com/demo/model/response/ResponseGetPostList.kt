package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseGetPostList(

	@field:SerializedName("images")
	val images: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("video")
	val video: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("viewCount")
	val viewCount: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("postLiked")
	val postLiked: Boolean? = null
)