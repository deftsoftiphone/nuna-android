package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProfilePosts(

    @field:SerializedName("postId")
    val postId: Int = 0,

    @field:SerializedName("imageUrl")
     val imageUrl: String = ""
):Serializable