package com.demo.model.request

import com.google.gson.annotations.SerializedName

data class RequestUpdate(

        @field:SerializedName("userId")
        var userId: Int? = 0,

        @field:SerializedName("notificationType")
        var notificationType: String? = "",

        @field:SerializedName("pageNumber")
        var pageNumber: Int = 0,



        @field:SerializedName("pageSize")
        var pageSize: Int? = 0
)