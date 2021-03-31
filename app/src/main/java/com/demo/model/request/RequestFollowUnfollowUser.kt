package com.demo.model.request

import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestFollowUnfollowUser(

    @field:SerializedName("followerUserId")
    var followerUserId: Int = 0,

    @field:SerializedName("follow")
    var follow: Boolean = false,

    @field:SerializedName("userId")
    var userId: Int = Prefs.init().loginData!!.userId

)