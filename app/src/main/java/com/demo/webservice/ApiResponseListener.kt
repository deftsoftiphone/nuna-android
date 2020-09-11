package com.demo.webservice

import com.demo.model.response.MasterResponse

interface ApiResponseListener {
    fun onApiCallSuccess(apiUrl: String, body: MasterResponse<*>) : Boolean
    fun onApiCallFailed(apiUrl: String, errCode: Int, errorMessage: String) : Boolean
}