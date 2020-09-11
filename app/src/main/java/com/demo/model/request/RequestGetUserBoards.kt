package com.demo.model.request


import com.demo.util.Prefs
import com.google.gson.annotations.SerializedName

data class RequestGetUserBoards(
    @SerializedName("userId")
    val userId: Int = Prefs.init().loginData!!.userId,
    @SerializedName("pageNumber")
    val pageNumber: Int = 0,
    @SerializedName("pageSize")
    val pageSize: Int = 10
)