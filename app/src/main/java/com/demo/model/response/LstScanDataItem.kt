package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LstScanDataItem(

	@field:SerializedName("Key")
	var key: String? = null,

	@field:SerializedName("Value")
	var value: String? = null,

	@field:SerializedName("Type")
	var type: Int = 0,

	@field:SerializedName("IsNewkey")
	var isNewkey: Boolean = false

) : Serializable