package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestGetPostDetails(

    @field:SerializedName("userId")
	var userId: Int = Prefs.init().loginData?.userId ?: 0,

    @field:SerializedName("postId")
	var postId: Int = 0

)