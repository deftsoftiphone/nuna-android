package com.demo.providers.aws

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener

interface AWSProvider {
    fun uploadFile(filePath: String, listener: TransferListener): String
}