package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestLikeUnLikePost(

	@field:SerializedName("postId")
	var postId: Int =0,

	@field:SerializedName("like")
	var like: Boolean = false,

	@field:SerializedName("likerUserId")
	var likerUserId: Int = Prefs.init().loginData!!.userId

)