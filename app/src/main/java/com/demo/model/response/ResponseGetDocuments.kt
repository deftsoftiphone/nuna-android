package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseGetDocuments(

	@field:SerializedName("lstDocument")
	val documents: List<Document>? = null
)