package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseFollowPopularUsersScreenData(

	@field:SerializedName("postList")
	val postList: List<Any?>? = null,

	@field:SerializedName("user")
	val user: User? = null
)