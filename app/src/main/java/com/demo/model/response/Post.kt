package com.demo.model.response

import com.demo.model.UserInfo
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Post(

	@field:SerializedName("user")
	val user: UserInfo = UserInfo(),

	@field:SerializedName("postId")
	val postId: Int = 0,

	@field:SerializedName("title")
	val title: String = "",

	@field:SerializedName("description")
	val description: String = "",

	@field:SerializedName("video")
	val video: String = "",

	@field:SerializedName("videoList")
	val videoList: List<String>? = null,

	@field:SerializedName("images")
	val images: String = "",

	@field:SerializedName("imageList")
	val imageList: List<String>? = null,

	@field:SerializedName("hashTags")
	val hashTags: String = "",

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null,

	@field:SerializedName("location")
	val location: Any? = null,

	@field:SerializedName("categoryIds")
	val categoryIds: String? = null,

	@field:SerializedName("categoryList")
	val categoryList: Any? = null,

	@field:SerializedName("createdDate")
	val createdDate: Any? = null,

	@field:SerializedName("updatedDate")
	val updatedDate: Any? = null,

	@field:SerializedName("likeCount")
	var likeCount: Int = 0,

	@field:SerializedName("shareCount")
	var shareCount: Int = 0,



	@field:SerializedName("viewCount")
	val viewCount: Int = 0,

	@field:SerializedName("active")
	val active: Boolean = false,

	@field:SerializedName("deleted")
	val deleted: Boolean = false,

	@field:SerializedName("postLiked")
	var postLiked: Boolean = false
):Serializable
