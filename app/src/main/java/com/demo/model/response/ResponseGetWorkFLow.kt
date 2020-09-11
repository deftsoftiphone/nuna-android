package com.demo.model.response

import com.demo.model.request.WorkFlowItem

import com.google.gson.annotations.SerializedName

data class ResponseGetWorkFLow(
	@field:SerializedName("lstWorkFlow")
	var workFlow: List<WorkFlowItem> = ArrayList()
)