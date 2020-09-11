package com.demo.model


import com.demo.model.response.Post
import com.google.gson.annotations.SerializedName

data class Pinboard(
    @SerializedName("pinboardId")
    val pinboardId: Int = 0,
    @SerializedName("userId")
    val userId: Int = 0,
    @SerializedName("pinboardName")
    var pinboardName: String = "",
    @SerializedName("createdDate")
    val createdDate: String = "",
    @SerializedName("postList")
    var postList: List<Post>? = emptyList()


)