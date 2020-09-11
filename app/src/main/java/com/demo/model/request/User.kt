package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("userId")
	var userId: Int? = 0
)