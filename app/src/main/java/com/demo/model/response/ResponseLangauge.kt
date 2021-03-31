package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseLangauge internal constructor(

	@field:SerializedName("name")
	val name: String? = "",

	@field:SerializedName("id")
	val id: Int? = 0,

	@field:SerializedName("isselect")
	var isselect: Boolean? = false
)