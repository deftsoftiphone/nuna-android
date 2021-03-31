package com.demo.fcm

import com.google.gson.annotations.SerializedName

data class User(

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("myCommentsCount")
	val myCommentsCount: Int? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("authToken")
	val authToken: Any? = null,

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("profileImage")
	val profileImage: String? = null,

	@field:SerializedName("socialType")
	val socialType: Int? = null,

	@field:SerializedName("paytmNumber")
	val paytmNumber: Any? = null,

	@field:SerializedName("followingCount")
	val followingCount: Int? = null,

	@field:SerializedName("membershipDate")
	val membershipDate: String? = null,

	@field:SerializedName("myLikesCount")
	val myLikesCount: Int? = null,

	@field:SerializedName("ifollow")
	val ifollow: Boolean? = null,

	@field:SerializedName("password")
	val password: Any? = null,

	@field:SerializedName("websiteUrl")
	val websiteUrl: String? = null,

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

	@field:SerializedName("otp")
	val otp: Any? = null,

	@field:SerializedName("userName")
	val userName: String? = "",

	@field:SerializedName("message")
	val message: Any? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("tags")
	val tags: String? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("profileId")
	val profileId: Int? = null,

	@field:SerializedName("dob")
	val dob: Any? = null,

	@field:SerializedName("socialId")
	val socialId: Any? = null,

	@field:SerializedName("acceptTC")
	val acceptTC: Boolean? = null,

	@field:SerializedName("location")
	val location: Any? = null,

	@field:SerializedName("myViewsCount")
	val myViewsCount: Int? = null,

	@field:SerializedName("userRole")
	val userRole: String? = null,

	@field:SerializedName("weeklyUpdates")
	val weeklyUpdates: Boolean? = null
)