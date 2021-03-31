package com.demo.model


import com.demo.model.response.Post
import com.google.gson.annotations.SerializedName

data class UserPostWrapper(
    @SerializedName("postList")
    var postList: List<Post> = listOf(),
    @SerializedName("user")
    var user: UserInfo = UserInfo()
)