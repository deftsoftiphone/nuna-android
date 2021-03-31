package com.demo.model.response.baseResponse

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FCMNotificationResponse(

    @field:SerializedName("notification")
    var notification: Notification? = null,

    @field:SerializedName("data")
    var data: NotificationData? = null
) : Parcelable

@Parcelize
data class NotificationData(

    @field:SerializedName("activityId")
    var activityId: String? = null,

    @field:SerializedName("postId")
    var postId: String? = null,

    @field:SerializedName("userId")
    var userId: String? = null,

    @field:SerializedName("title")
    var title: String? = null,

    @field:SerializedName("body")
    var text: String? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("media")
    var media: String? = null,

    @field:SerializedName("description")
    var description: String? = null,

    @field:SerializedName("notificationId")
    var notificationId: Int? = 0

) : Parcelable

@Parcelize
data class Notification(

    @field:SerializedName("icon")
    var icon: String? = null
) : Parcelable
