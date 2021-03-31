package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName


data class RequestUserRegister(

//	@field:SerializedName("email")
//	var email: String? = "",

//	@field:SerializedName("phoneNumber")
//	var phoneNumber: String? = "",


//	@field:SerializedName("userName")
//	var userName: String? = "",

    //@field:SerializedName("lastName")
    //var lastName: String? = "",

//	@field:SerializedName("password")
//	var password: String? = "",

//    @field:SerializedName("countryCallingCode")
//    var countryCallingCode: String? = "91",

    @field:SerializedName("countryCode")
    var countryCode: String? = "91",

//    @field:SerializedName("fcmToken")
//    var fcmToken: String? = Prefs.init().deviceToken,

    @field:SerializedName("mobileNo")
    var mobileNo: String? = ""

//    @field:SerializedName("otpCode")
//    var otpCode: Number? = null,
//
//    @field:SerializedName("languageId")
//    var languageId: String? = Prefs.init().selectedLang._id
)

