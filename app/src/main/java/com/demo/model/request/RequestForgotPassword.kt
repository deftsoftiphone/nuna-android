package com.demo.model.request

import com.google.gson.annotations.SerializedName


data class RequestForgotPassword(

    @field:SerializedName("email")
	var email: String? = /*Constant.DUMMY_EMAIL*/""


)