package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseFollowingFollowerUsersList(

	@field:SerializedName("profileId")
	val profileId: Int? = null,

	@field:SerializedName("firstName")
	val firstName: String? = null,

	@field:SerializedName("lastName")
	val lastName: String? = null,

	@field:SerializedName("profileImage")
	val profileImage: String? = null,

	@field:SerializedName("websiteUrl")
	val websiteUrl: String? = null,

	@field:SerializedName("bio")
	val bio: String? = null,

	@field:SerializedName("tags")
	val tags: String? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("countryCallingCode")
	val countryCallingCode: String? = null,

	@field:SerializedName("latitude")
	val latitude: Any? = null,

	@field:SerializedName("longitude")
	val longitude: Any? = null,

	@field:SerializedName("location")
	val location: Any? = null,

	@field:SerializedName("gender")
	val gender: Any? = null,

	@field:SerializedName("dob")
	val dob: Any? = null,

	@field:SerializedName("paytmNumber")
	val paytmNumber: Any? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("userName")
	val userName: Any? = null,

	@field:SerializedName("password")
	val password: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("authKey")
	val authKey: Any? = null,

	@field:SerializedName("userRole")
	val userRole: String? = null,

	@field:SerializedName("searchKeyWord")
	val searchKeyWord: Any? = null,

	@field:SerializedName("membershipDate")
	val membershipDate: String? = null,

	@field:SerializedName("isInstantAlerts")
	val isInstantAlerts: Boolean? = null,

	@field:SerializedName("socialId")
	val socialId: Any? = null,

	@field:SerializedName("socialType")
	val socialType: Int? = null,

	@field:SerializedName("message")
	val message: Any? = null,

	@field:SerializedName("authToken")
	val authToken: Any? = null,

	@field:SerializedName("followerCount")
	val followerCount: Int? = null,

	@field:SerializedName("followingCount")
	val followingCount: Int? = null,

	@field:SerializedName("myCommentsCount")
	val myCommentsCount: Int? = null,

	@field:SerializedName("myLikesCount")
	val myLikesCount: Int? = null,

	@field:SerializedName("myViewsCount")
	val myViewsCount: Int? = null,

	@field:SerializedName("otp")
	val otp: Any? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("acceptTC")
	val acceptTC: Boolean? = null,

	@field:SerializedName("weeklyUpdates")
	val weeklyUpdates: Boolean? = null,

	@field:SerializedName("ifollow")
	val ifollow: Boolean? = null,

	@field:SerializedName("fcmToken")
	val fcmToken: Any? = null,

	@field:SerializedName("subscribedNewsletter")
	val subscribedNewsletter: Boolean? = null
)