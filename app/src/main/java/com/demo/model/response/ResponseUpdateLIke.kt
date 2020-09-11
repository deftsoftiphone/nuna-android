package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseUpdateLIke(

	@field:SerializedName("notificationId")
	val notificationId: Int? = null,

	@field:SerializedName("notificationType")
	val notificationType: String? = null,

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("notificationDate")
	val notificationDate: String? = null
)