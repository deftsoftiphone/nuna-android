package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ResponseNotificationComment(

        @field:SerializedName("notificationId")
        val notificationId: Int? = null,

        @field:SerializedName("notificationType")
        val notificationType: String? = null,

        @field:SerializedName("postId")
        val postId: Int? = null,

        @field:SerializedName("notificationDate")
        val notificationDate: String? = null,

        @field:SerializedName("user")
        val user: User? = null
): Serializable