package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class FollowHashTagRequest(
    @SerializedName("hashTagId")
    var hashTagId: String? = null

)