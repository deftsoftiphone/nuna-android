package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestChangePassword(

	@field:SerializedName("password")
	var oldPassword: String = "",

	@field:SerializedName("newpassword")
	var newPassword: String = "",

	@field:SerializedName("email")
	var email: String = "",



	@Transient var confirmPassword: String = ""
)