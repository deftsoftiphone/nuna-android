package com.demo.model.request


import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestActionOnPost(
    @SerializedName("postId")
    var postId: Int = 0,
    @SerializedName("action")
    var action: String = "report",
    @SerializedName("userId")
    var userId: Int = Prefs.init().loginData!!.userId
)