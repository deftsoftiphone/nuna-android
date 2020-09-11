package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResponseUploadDocuments(

	@field:SerializedName("DocumentId")
	var documentId: Int = 0,

	@field:SerializedName("DocUrls")
	var docUrls: List<String>? = emptyList(),

	@field:SerializedName("lstScanData")
	var lstScanData: List<RawKeyValuePair>? = emptyList(),

	@field:SerializedName("lstValidationKeys")
	var lstValidationKeys: List<String>? = emptyList(),

	@field:SerializedName("ApproverId")
	var approverId: String? = null,

	@field:SerializedName("EditByUserId")
	var editByUserId: String? = null

) : Serializable