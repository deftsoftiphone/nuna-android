package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName


data class RequestValidate(

    @field:SerializedName("phoneNumber")
    var phoneNumber: String? = "",

    @field:SerializedName("mobileNo")
    var mobileNo: String? = "",

    @field:SerializedName("countryCallingCode")
    var countryCallingCode: String? = "",

    @field:SerializedName("countryCode")
    var countryCode: String? = "",

    @field:SerializedName("otp")
    var otp: String? = "",

    @field:SerializedName("otpCode")
    var otpCode: String? = "",

    @field:SerializedName("languageId")
    var languageId: String? = Prefs.init().selectedLang._id,

    @field:SerializedName("registrationToken")
    var registrationToken: String? = Prefs.init().deviceToken,

    @field:SerializedName("deviceType")
    var deviceType: String? = "android"
)