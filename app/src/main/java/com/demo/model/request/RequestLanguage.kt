package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestLanguage(

	@field:SerializedName("languageId")
	var languageId: Int? = 0,

	@field:SerializedName("userId")
	var userId: Int? = 0
)