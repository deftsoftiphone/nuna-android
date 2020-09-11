package com.demo.model

import com.google.gson.annotations.SerializedName

data class 	UserProfile(

	@field:SerializedName("Prefix")
	var prefix: String = "",

	@field:SerializedName("FirstName")
	var firstName: String = "",

	@field:SerializedName("LastName")
	var lastName: String = "",

	@field:SerializedName("Phonenumber")
	var phonenumber: String = "",

	@field:SerializedName("Email")
	var email: String = "",

	@field:SerializedName("RoleName")
	var roleName: String = "",

	@field:SerializedName("ProfilePicture")
	var profilePicture: String = "",

	@field:SerializedName("DepartmentName")
	var departmentName: String = ""
)