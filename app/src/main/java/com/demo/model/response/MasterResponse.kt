package com.demo.model.response

import com.google.gson.annotations.SerializedName
import java.util.Collections.emptyList

data class MasterResponse<T>(

        @field:SerializedName("responsePacket")
        var data: T? = null,

        @field:SerializedName("statusCode")
        var responseCode: Int = 0,

        @field:SerializedName("message")
        var successMsg: String = "",

        @field:SerializedName("ValidationErrors")
        var validationErrors: List<String> = emptyList(),

        @field:SerializedName("ApiName")
        var apiName: String? = null
)