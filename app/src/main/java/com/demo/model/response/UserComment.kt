package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserComment(

    @field:SerializedName("parentCommentId")
    val parentCommentId: Int? = null,

    @field:SerializedName("active")
    val active: Boolean? = null,

    @field:SerializedName("likeCount")
    var likeCount: Int? = null,

    @field:SerializedName("postId")
    val postId: Int? = null,

    @field:SerializedName("commentText")
    var commentText: String? = null,

    @field:SerializedName("url")
    var url: Boolean = true,

    @field:SerializedName("replyCount")
    var replyCount: Int? = null,

    @field:SerializedName("createdDate")
    val createdDate: String? = null,

    @field:SerializedName("deleted")
    val deleted: Boolean? = null,
    @field:SerializedName("commentLiked")
    var commentLiked: Boolean = false,

    @field:SerializedName("commentId")
    val commentId: Int? = null,

    @field:SerializedName("user")
    var user: User? = null,

    @field:SerializedName("updatedDate")
    val updatedDate: Any? = null,

    @field:SerializedName("childComment")
    var childComment: ArrayList<UserComment>? = null
) : Serializable