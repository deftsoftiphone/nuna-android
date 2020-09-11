package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseLogin(







	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("authToken")
	val authToken: String? = null,

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("profileImage")
	val profileImage: String? = null,

	@field:SerializedName("socialType")
	val socialType: Int? = null,

	@field:SerializedName("membershipDate")
	val membershipDate: String? = null,

	@field:SerializedName("password")
	val password: Any? = null,

	@field:SerializedName("websiteUrl")
	val websiteUrl: Any? = null,

	@field:SerializedName("countryCallingCode")
	val countryCallingCode: String? = null,

	@field:SerializedName("followerCount")
	val followerCount: Int? = null,

	@field:SerializedName("fcmToken")
	val fcmToken: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("subscribedNewsletter")
	val subscribedNewsletter: Boolean? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null,

	@field:SerializedName("searchKeyWord")
	val searchKeyWord: Any? = null,

	@field:SerializedName("authKey")
	val authKey: Any? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("isInstantAlerts")
	val isInstantAlerts: Boolean? = null,

	@field:SerializedName("userName")
	var userName: String? = "",



	@field:SerializedName("userId")
	val userId: Int = 0,

	@field:SerializedName("tags")
	val tags: Any? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("profileId")
	val profileId: Int? = null,

	@field:SerializedName("socialId")
	val socialId: Any? = null,

	@field:SerializedName("acceptTC")
	val acceptTC: Boolean? = null,

	@field:SerializedName("location")
	val location: Any? = null,

	@field:SerializedName("userRole")
	val userRole: String? = null,


	@field:SerializedName("accesskey")
	val accesskey: String? = null,

	@field:SerializedName("secretKey")
	val secretKey: String? = null,


	@field:SerializedName("weeklyUpdates")
	val weeklyUpdates: Boolean? = null
)