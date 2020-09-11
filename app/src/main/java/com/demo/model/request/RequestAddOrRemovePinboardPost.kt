package com.demo.model.request


import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestAddOrRemovePinboardPost(
    @SerializedName("action")
    var action: String = "",
    @SerializedName("pinboardId")
    var pinboardId: Int = 0,
    @SerializedName("postId")
    var postId: Int = 0,
    @SerializedName("userId")
    var userId: Int = Prefs.init().loginData!!.userId
)