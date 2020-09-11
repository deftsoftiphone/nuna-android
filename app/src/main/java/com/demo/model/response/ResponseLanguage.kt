package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseLanguage(

	@field:SerializedName("languageId")
	val languageId: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)