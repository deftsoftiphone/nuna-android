package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class Language(

	@field:SerializedName("languageId")
	var languageId: String? = null
)