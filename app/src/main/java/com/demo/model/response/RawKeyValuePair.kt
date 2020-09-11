package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RawKeyValuePair(

	@field:SerializedName("Key")
	var key: String? = null,

	@field:SerializedName("Value")
	var value: String? = null,

	@field:SerializedName("IsNewkey")
	var isNewkey: Boolean = false,

	@field:SerializedName("IsAdditionalKey")
	var isAdditionalKey: Boolean = false

) : Serializable