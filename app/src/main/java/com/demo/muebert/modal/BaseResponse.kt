package com.demo.muebert.modal

import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PaginateItem(
        @field:SerializedName("offset")
        var offset: Int = 0,
        @field:SerializedName("from")
        var from: String? = null
) : Parcelable

@Parcelize
data class BaseRequest(
        @field:SerializedName("method")
        var method: String? = null,

        @field:SerializedName("params")
        var params: Params? = null
) : Parcelable

@Parcelize
data class Params(

        @field:SerializedName("license")
        var license: String? = null,

        @field:SerializedName("email")
        var email: String? = null,

        @field:SerializedName("token")
        var token: String? = null,

        @field:SerializedName("pat")
        var pat: String? = null,

        @field:SerializedName("playlist")
        var playlist: String? = null,

        @field:SerializedName("duration")
        var duration: String? = null,

        @field:SerializedName("format")
        var format: String? = null,

        @field:SerializedName("intensity")
        var intensity: String? = null,

        @field:SerializedName("bitrate")
        var bitrate: String? = null,

        @field:SerializedName("mode")
        var mode: String? = null
) : Parcelable


@Parcelize
data class BaseResponse(

        @field:SerializedName("api_ver")
        var apiVer: String? = null,

        @field:SerializedName("method")
        var method: String? = null,

        @field:SerializedName("data")
        var data: Data? = null,

        @field:SerializedName("status")
        var status: Int? = null,

        @field:SerializedName("error")
        var error: Error? = null
) : Parcelable

@Parcelize
data class Data(

        @field:SerializedName("pat")
        var pat: String? = null,

        @field:SerializedName("code")
        var code: Int? = null,

        @field:SerializedName("text")
        var text: String? = null,

        @field:SerializedName("available_intensities")
        var availableIntensities: ArrayList<AvailableIntensitiesItem>? = null,

        @field:SerializedName("categories")
        var categories: ArrayList<CategoriesItem>? = null,

        @field:SerializedName("available_bitrates")
        var availableBitrates: ArrayList<AvailableBitratesItem>? = null,

        @field:SerializedName("tasks")
        var tasks: ArrayList<TasksItem?>? = null,

        @field:SerializedName("download_link")
        var download_link: String? = null

) : Parcelable

@Parcelize
data class Error(
        @field:SerializedName("code")
        var code: Int? = null,

        @field:SerializedName("text")
        var text: String? = null
) : Parcelable


@Parcelize
data class AvailableBitratesItem(

        @field:SerializedName("bitrate")
        var bitrate: Int? = null
) : Parcelable

@Parcelize
data class CategoriesItem(

        @field:SerializedName("category_id")
        var categoryId: Int? = null,

        @field:SerializedName("playlist")
        var playlist: String? = null,

        @field:SerializedName("stream")
        var stream: Stream? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("groups")
        var groups: ArrayList<GroupsItem>? = null,

        @field:SerializedName("channels")
        var channels: ArrayList<ChannelsItem>? = null,

        var icon: @RawValue Drawable? = null

) : Parcelable

@Parcelize
data class ChannelsItem(

        @field:SerializedName("playlist")
        var playlist: String? = null,

        @field:SerializedName("emoji")
        var emoji: String? = null,

        @field:SerializedName("stream")
        var stream: Stream? = null,

        @field:SerializedName("name")
        var name: String? = null,

        @field:SerializedName("group_name")
        var groupName: String? = null,

        @field:SerializedName("categoryName")
        var categoryName: String? = null,

        @field:SerializedName("icon")
        var icon: String? = null,

        @field:SerializedName("channel_id")
        var channelId: Int? = null,

        @field:SerializedName("group_id")
        var groupId: Int? = null,

        @field:SerializedName("category_id")
        var categoryId: Int? = null,

        var isPlaying: Boolean? = false,

        var isSelected: Boolean? = false
) : Parcelable

@Parcelize
data class Stream(

        @field:SerializedName("url")
        var url: String? = null
) : Parcelable

@Parcelize
data class AvailableIntensitiesItem(

        @field:SerializedName("intensity")
        var intensity: String? = null
) : Parcelable

@Parcelize
data class GroupsItem(

        @field:SerializedName("playlist")
        var playlist: String? = null,

        @field:SerializedName("channels")
        var channels: ArrayList<ChannelsItem>? = null,

        @field:SerializedName("group_id")
        var groupId: Int? = null,

        @field:SerializedName("stream")
        var stream: Stream? = null,

        @field:SerializedName("name")
        var name: String? = null
) : Parcelable

@Parcelize
data class TasksItem(
        @field:SerializedName("download_link")
        var downloadLink: String? = null,

        @field:SerializedName("task_status_code")
        var taskStatusCode: Int? = null,

        @field:SerializedName("task_id")
        var taskId: String? = null,

        @field:SerializedName("task_status_text")
        var taskStatusText: String? = null
) : Parcelable

@Parcelize
data class UserFollowStatus(
        @field:SerializedName("userId")
        var userId: String? = null,

        @field:SerializedName("isFollowed")
        var followedByMe: Boolean? = null

) : Parcelable
