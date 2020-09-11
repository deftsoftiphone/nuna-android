package com.demo.model.response

import com.google.gson.annotations.SerializedName

data class ResponseUpdateKeyValues(

	@field:SerializedName("lstNotMatchCondition")
	val lstNotMatchCondition: List<DetailKeyValuePair>? = null,

	@field:SerializedName("lstFailCondition")
	val lstFailCondition: List<DetailKeyValuePair>? = null,

	@field:SerializedName("lstNewkeyValue")
	val lstNewKeyValue: List<RawKeyValuePair>? = null,

	@field:SerializedName("WorkflowId")
	val workflowId: Int = 0,

	@field:SerializedName("DocumentId")
	val documentId: Int = 0,

	@field:SerializedName("IsUpdate")
	val isUpdate: Boolean = false,

	@field:SerializedName("WorkFlowExecutionOn")
	val workFlowExecutionOn: Int = 0
)