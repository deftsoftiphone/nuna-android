package com.demo.model.request

import com.google.gson.annotations.SerializedName


data class RequestSaveUserProfile(

	@field:SerializedName("firstName")
	var firstName: String? = null,

	@field:SerializedName("lastName")
	var lastName: String? = null,

	@field:SerializedName("userName")
	var userName: String? = null,

	@field:SerializedName("profileImage")
	var profileImage: String? = null,

	@field:SerializedName("websiteUrl")
	var websiteUrl: String? = null,

	@field:SerializedName("bio")
	var bio: String? = "Hey i am using Nuna",

	@field:SerializedName("tags")
	var tags: String? = null,

	@field:SerializedName("phoneNumber")
	var phoneNumber: String? = null,

	@field:SerializedName("countryCallingCode")
	var countryCallingCode: String? = null,

	@field:SerializedName("latitude")
	var latitude: Any? = null,

	@field:SerializedName("longitude")
	var longitude: Any? = null,

	@field:SerializedName("location")
	var location: String? = null,

	@field:SerializedName("userId")
	var userId: Int? = null,

	@field:SerializedName("email")
	var email: String? = null,

	@field:SerializedName("dob")
	var dob: String = "",

	@field:SerializedName("gender")
	var gender: String = ""
)