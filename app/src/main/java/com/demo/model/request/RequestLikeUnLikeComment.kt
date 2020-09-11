package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestLikeUnLikeComment(

    @field:SerializedName("likerUserId")
    var likerUserId: Int = 0,

    @field:SerializedName("commentId")
    var commentId: Int = 0,

    @field:SerializedName("like")
    var like: Boolean = false
)