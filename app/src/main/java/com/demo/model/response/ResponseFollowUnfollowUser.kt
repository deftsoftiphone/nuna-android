package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseFollowUnfollowUser(

    @field:SerializedName("follow")
    val follow: Boolean = false,
    @field:SerializedName("followerUserId")
    val followerUserId: Int = 0,
    @field:SerializedName("userId")
    val userId: Int = 0

)