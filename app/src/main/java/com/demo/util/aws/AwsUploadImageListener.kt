package com.demo.util.aws

interface  AwsUploadImageListener{

    fun onImageUploadSuccess()
    fun onImageUploadFail(error:String)

    fun  onUploadProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long)
}