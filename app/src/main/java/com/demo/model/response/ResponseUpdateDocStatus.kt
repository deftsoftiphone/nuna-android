package com.demo.model.response


import com.demo.R
import com.demo.base.MainApplication
import com.google.gson.annotations.SerializedName

data class ResponseUpdateDocStatus(

    @field:SerializedName("DocumentId")
    var documentId: Int? = 0,

    @field:SerializedName("Apporver")
    var apporver: String? = "",

    @field:SerializedName("Status")
    var status: Int? = 0

) {
    companion object {
        const val STATUS_APPROVE = 1
        const val STATUS_REJECT = 2
    }

    fun getStatusStr(): String {
        return when (status) {
            STATUS_APPROVE -> MainApplication.get().getString(R.string.approved)
            STATUS_REJECT -> MainApplication.get().getString(R.string.rejected)
            else -> MainApplication.get().getString(R.string.approved)
        }
    }
}