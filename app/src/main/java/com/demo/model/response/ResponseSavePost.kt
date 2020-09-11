package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseSavePost(

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("video")
	val video: String? = null,

	@field:SerializedName("videoList")
	val videoList: Any? = null,

	@field:SerializedName("images")
	val images: String? = null,

	@field:SerializedName("imageList")
	val imageList: Any? = null,

	@field:SerializedName("hashTags")
	val hashTags: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("categoryIds")
	val categoryIds: String? = null,

	@field:SerializedName("categoryList")
	val categoryList: Any? = null,

	@field:SerializedName("createdDate")
	val createdDate: String? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: String? = null,

	@field:SerializedName("likeCount")
	val likeCount: Int? = null,

	@field:SerializedName("viewCount")
	val viewCount: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("deleted")
	val deleted: Boolean? = null,

	@field:SerializedName("postLiked")
	val postLiked: Boolean? = null
)