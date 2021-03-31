package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName


data class RequestGetCommentsList(

    @field:SerializedName("userId")
	val userId: Int? = Prefs.init().loginData?.userId,

    @field:SerializedName("postId")
	val postId: Int? = 0,

    @field:SerializedName("pageNumber")
	val pageNumber: Int? = 0,

    @field:SerializedName("pageSize")
	val pageSize: Int? = 20
)