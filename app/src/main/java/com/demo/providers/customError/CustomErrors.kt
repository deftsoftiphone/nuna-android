package com.demo.providers.customError

interface CustomErrors {
    fun otpErrorMsg(defaultMsg:String?):String?
    fun postErrorMsg(defaultMsg:String?):String?
}