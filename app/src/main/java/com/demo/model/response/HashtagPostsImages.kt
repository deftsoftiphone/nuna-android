package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class HashtagPostsImages (

    @field:SerializedName("imageList")
    val image: ArrayList<String>
)
