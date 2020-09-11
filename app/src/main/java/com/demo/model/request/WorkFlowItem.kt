package com.demo.model.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WorkFlowItem(

	@field:SerializedName("BusinessId")
	var businessId: Int = 0,

	@field:SerializedName("WorkFlowId")
	var workFlowId: Int = 0,

	@field:SerializedName("Description")
	var description: String = "",

	@field:SerializedName("WorkFlowName")
	var workFlowName: String = ""
) : Serializable