package com.demo.util.aws

import android.util.Log
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.demo.base.MainApplication
import com.demo.util.Prefs
import java.io.File


object AmazonUtil : AWSCredentials, TransferListener {
    //Live
   // const val S3_MEDIA_PATH = "https://hookup-post-images-prod.s3.ap-south-1.amazonaws.com/"
   //const val cloud_MEDIA_PATH = "https://d10it2ruaxa4qh.cloudfront.net/"
   // const val S3_MEDIA_PATH_VIDEO = "https://hookup-post-videos-prod.s3.ap-south-1.amazonaws.com/"
   // const val cloud_Video_PATH = "https://dx2j3e7co13d8.cloudfront.net/"
    //const val GIF_Video = "https://hookup-post-videos-uat.s3/"

    //uat
    const val S3_MEDIA_PATH = "https://hookup-post-images-uat.s3.ap-south-1.amazonaws.com/"
    const val cloud_MEDIA_PATH = "http://dl4xh5ad70pj8.cloudfront.net/"
    const val S3_MEDIA_PATH_VIDEO = "https://hookup-post-videos-uat.s3.ap-south-1.amazonaws.com/"
   const val cloud_Video_PATH = "http://dyobv2v8fqzkz.cloudfront.net/"
    const val GIF_Video = "https://hookup-post-videos-uat.s3"

    const val S3_MEDIA_CUSTOMER_AVTAR = "media_"
    const val S3_MEDIA_AUDIO = "media/audio/"
    const val S3_MEDIA_VIDEO = "video/"
    var awsUploadImageListener: AwsUploadImageListener?=null


    fun initAmazonClient() {

        AWSMobileClient.getInstance().initialize(MainApplication.get().getContext(), object :
            Callback<UserStateDetails> {
            override fun onResult(result: UserStateDetails?) {
                Log.d("aws", "success")
            }

            override fun onError(e: Exception?) {
                Log.d("aws", "error")

            }

        })

    }


    private fun getS3Client(): AmazonS3Client {
        val configuration = ClientConfiguration()

        configuration.maxErrorRetry = 3
        configuration.connectionTimeout = 50*1000
        configuration.socketTimeout = 50*1000
        configuration.protocol = Protocol.HTTP
        return AmazonS3Client(this, com.amazonaws.regions.Region.getRegion("ap-south-1"),configuration)
    }
   // private let mobileClient = AWSMobileClient(environment: String = "Default")

    private fun getTransferUtility(): TransferUtility {

       if(Prefs.init().isVideoSelect){
           AWSMobileClient.getInstance().configuration.configuration="Video"
           Prefs.init().isVideoSelect=false
       }else{
           AWSMobileClient.getInstance().configuration.configuration="Default"
       }
        //


        return TransferUtility.builder()
            .context(MainApplication.get().getContext())
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(getS3Client())
          //  .defaultBucket("Video")
            .build()
    }


    fun uploadFile(filePath: String, filename: String, awsUploadImageListener: AwsUploadImageListener) {
        this.awsUploadImageListener=awsUploadImageListener
        val uploadObserver= getTransferUtility().upload(
            filename,
            File(filePath), CannedAccessControlList.PublicRead
        )
        uploadObserver.setTransferListener(this)
    }


    override fun getAWSAccessKeyId(): String {
        return Prefs.init().accesskeyId
    }

    override fun getAWSSecretKey(): String {
        return Prefs.init().secretKey
    }


    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
        Log.d("transfer total "+bytesTotal, "progress "+bytesCurrent)

        awsUploadImageListener?.onUploadProgressChanged(id,bytesCurrent,bytesTotal)
    }

    override fun onStateChanged(id: Int, state: TransferState?) {
        if (TransferState.COMPLETED == state) {
           awsUploadImageListener?.onImageUploadSuccess()
        }
    }

    override fun onError(id: Int, ex: java.lang.Exception?) {
        awsUploadImageListener?.onImageUploadFail(ex?.message?:"")

    }
/*
"Bucket":"hookup-post-images-prod" ,*/

}

