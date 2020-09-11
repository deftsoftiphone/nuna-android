package com.demo.model.response

import com.demo.model.UserInfo
import com.google.gson.annotations.SerializedName

data class HashtagPosts(
    @field:SerializedName("hName")
    val hName: String,
    @field:SerializedName("hCount")
    val hCount: String,
    @field:SerializedName("hImages")
    val hImages: List<String>

    )
