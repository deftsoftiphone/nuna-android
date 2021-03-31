package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailKeyValuePair(

	@field:SerializedName("Key")
	val key: Int? = null,

	@field:SerializedName("Operator")
	val operator: String? = null,

	@field:SerializedName("Value1")
	val value1: String? = null,

	@field:SerializedName("Value2")
	val value2: String? = null,

	@field:SerializedName("KeyType")
	val keyType: Int? = null,

	@field:SerializedName("KeyName")
	val keyName: String? = null
)