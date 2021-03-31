package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestGetFollowingFollowerUsersList(

	@field:SerializedName("userId")
	var userId: Int = 0,

	@field:SerializedName("type")
	var type: String? = "following",

	@field:SerializedName("pageNumber")
	var pageNumber: Int = 0,

	@field:SerializedName("pageSize")
	var pageSize: Int = 0
)