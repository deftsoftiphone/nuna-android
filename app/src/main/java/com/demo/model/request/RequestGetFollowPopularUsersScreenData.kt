package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestGetFollowPopularUsersScreenData(

	@field:SerializedName("userId")
	var userId: Int =0
)