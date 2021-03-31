package com.demo.webservice

data class WrapperApiError (val apiUrl : String = "", val resultCode : Int = 0, val msg : String = "", var validationErr : List<String> = emptyList<String>())