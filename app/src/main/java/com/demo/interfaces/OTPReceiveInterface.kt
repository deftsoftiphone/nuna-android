package com.demo.interfaces

interface OTPReceiveInterface {
    fun onOtpReceived(otp : String)
    fun onOtpTimeout()
}