package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseAddUserCategory(

	@field:SerializedName("categoryIds")
    val categoryIds: String? = null,

@field:SerializedName("userId")
val userId: Int? = null
)