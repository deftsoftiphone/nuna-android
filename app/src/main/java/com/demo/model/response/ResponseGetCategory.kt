package com.demo.model.response

import com.google.gson.annotations.SerializedName


data class ResponseGetCategory(

    @field:SerializedName("responsePacket")
    val documents: GetCategory? = null

)