package com.demo.model

import com.google.gson.annotations.SerializedName

data class BusinessProfile(

	@field:SerializedName("BusinessName")
	var businessName: String = "",

	@field:SerializedName("BusiessEmail")
	var busiessEmail: String = "",

	@field:SerializedName("BusinessAddress")
	var businessAddress: String = "",

	@field:SerializedName("BusinessContact")
	var businessContact: String = "",

	@field:SerializedName("BusinessPicture")
	var businessPicture: String = ""
)