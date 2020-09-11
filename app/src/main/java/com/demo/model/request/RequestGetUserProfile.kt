package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestGetUserProfile(

	@field:SerializedName("userId")
	var userId: Int = Prefs.init().loginData!!.userId

)