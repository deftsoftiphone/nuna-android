package com.demo.model.request

import com.demo.util.Constant
import com.demo.util.Prefs

import com.google.gson.annotations.SerializedName


data class RequestLogin(


    @field:SerializedName("email")
	var email: String = Constant.DUMMY_EMAIL,

    @field:SerializedName("password")
	var password: String = Constant.DUMMY_PASSWORD,

    @field:SerializedName("fcmToken")
	val deviceToken: String = Prefs.init().deviceToken,

    @field:SerializedName("DeviceType")
	val deviceType: String = "Android",

    @Transient var flagRememberMe : Boolean = Prefs.init().rememberMe
)