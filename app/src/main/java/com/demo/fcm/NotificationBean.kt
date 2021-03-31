package com.demo.fcm

import com.google.gson.annotations.SerializedName

data class NotificationBean(

	@field:SerializedName("notificationId")
	val notificationId: Int? = null,

	@field:SerializedName("notificationType")
	val notificationType: String? = null,

	@field:SerializedName("postId")
	val postId: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("msg")
	var msg: String? = "",

	@field:SerializedName("notificationDate")
	val notificationDate: String? = null
)