package com.demo.model.response

import androidx.recyclerview.widget.DiffUtil
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Document(

    @field:SerializedName("DocumentId")
    var documentId: Int = 0,

    @field:SerializedName("TotalPages")
    var totalPages: Long = 0,

    @field:SerializedName("Title")
    var title: String? = "",

    @field:SerializedName("WorkFlowName")
    var workFlowName: String? = "",

    @field:SerializedName("WorkFlowId")
    var workFlowId: Int = 0,

    @field:SerializedName("BusinessName")
    var businessName: String? = "",

    @field:SerializedName("Status")
    var status: String? = "",

    @field:SerializedName("Approver")
    var approver: String? = "",

    @field:SerializedName("ApprovedId")
    var approvedId: String? = "",

    @field:SerializedName("UploadedDate")
    var uploadedDate: String? = "",

    @field:SerializedName("ApprovedDate")
    var approvedDate: String? = "",

    @field:SerializedName("lstDocUrls")
    var lstDocUrls: List<String>? = null,

    @field:SerializedName("lstKeyValue")
    var lstKeyValue: List<RawKeyValuePair>? = null,

    @field:SerializedName("ApproverId")
    var approverId: String? = null,

    @field:SerializedName("ShowFormId")
    var showFormId: String? = null,

    @field:SerializedName("DocumentTaskCount")
    var documentTaskCount: Int = 0

) : Serializable {

    fun update(data : ResponseUpdateDocStatus){
        this.status = data.getStatusStr()
        this.approver = data.apporver ?: ""
    }

    companion object {
        var DIFF_CALLBACK: DiffUtil.ItemCallback<Document> =
            object : DiffUtil.ItemCallback<Document>() {

                override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                    return oldItem.documentId == newItem.documentId
                }

                override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                    return oldItem == newItem
                }
            }
    }

}