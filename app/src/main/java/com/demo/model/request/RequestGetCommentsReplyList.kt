package com.demo.model.request

import com.google.gson.annotations.SerializedName


data class RequestGetCommentsReplyList(

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("commentId")
	val commentId: Int? = null,

	@field:SerializedName("pageNumber")
	val pageNumber: Int? = null,

	@field:SerializedName("pageSize")
	val pageSize: Int? = null
)