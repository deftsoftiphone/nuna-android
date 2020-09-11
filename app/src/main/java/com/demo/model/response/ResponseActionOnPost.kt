package com.demo.model.response


import com.google.gson.annotations.SerializedName

data class ResponseActionOnPost(
    @SerializedName("action")
    var action: String = "",
    @SerializedName("actionStatus")
    var actionStatus: String = "",
    @SerializedName("categoryId")
    var categoryId: Int = 0,
    @SerializedName("comments")
    var comments: Any = Any(),
    @SerializedName("postId")
    var postId: Int = 0,
    @SerializedName("userId")
    var userId: Int = 0
)