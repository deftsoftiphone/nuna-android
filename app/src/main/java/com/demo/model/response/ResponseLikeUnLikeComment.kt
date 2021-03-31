package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseLikeUnLikeComment(

	@field:SerializedName("commentId")
	val commentId: Int? = null,

	@field:SerializedName("likerUserId")
	val likerUserId: Int? = null,

	@field:SerializedName("like")
	val like: Boolean? = null
)