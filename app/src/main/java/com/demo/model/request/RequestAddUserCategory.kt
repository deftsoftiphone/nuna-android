package com.demo.model.request

import com.google.gson.annotations.SerializedName


data class RequestAddUserCategory(

	@field:SerializedName("userId")
	val userId: Int = 0,

	@field:SerializedName("categoryIds")
	val categoryIds: String = ""
)