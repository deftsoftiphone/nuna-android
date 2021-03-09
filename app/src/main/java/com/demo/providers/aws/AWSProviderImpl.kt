package com.demo.providers.aws

import android.content.Context
import android.webkit.MimeTypeMap
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import java.io.File
import java.util.*


private const val AWS_REGION = "ap-south-1"

//private const val S3_BUCKET = "vod-watchfolder-nunomindcrew"
private const val S3_BUCKET = "vod-mediabucketnunomindcrew"

//private const val POOL_ID = "ap-south-1:8649a798-31af-43cf-8005-6e681c215760"
private const val NEW_POOL_ID = "ap-south-1:2d4c29a9-73e5-4ead-b2b6-f841ecbbc335"
private const val DIRECTORY = "inputs"


class AWSProviderImpl(private val context: Context) : AWSProvider {
    private lateinit var s3Client: AmazonS3

    init {
        setupClient()

    }

    private fun setupClient() {
        val configuration = ClientConfiguration()
        configuration.maxErrorRetry = 3
        configuration.connectionTimeout = 50 * 1000
        configuration.socketTimeout = 50 * 1000
        configuration.protocol = Protocol.HTTP

        val cred = CognitoCachingCredentialsProvider(
            context,
            NEW_POOL_ID,
            Regions.AP_SOUTH_1,
        )

        s3Client = AmazonS3Client(
            cred, Region.getRegion(AWS_REGION), configuration
        )

    }

    override fun uploadFile(
        filePath: String,
        listener: TransferListener
    ): String {
        val fileName = UUID.randomUUID().toString()
        var extension = MimeTypeMap.getFileExtensionFromUrl(filePath)
//        if (extension == "mp4") extension = "m3u8"


        var key = "$DIRECTORY/$fileName"
        key = "$key.${extension}"

        TransferNetworkLossHandler.getInstance(context)
        val uploadObserver = getTransferUtility().upload(
            S3_BUCKET,
            key,
            File(filePath),
            CannedAccessControlList.PublicRead
        )
        uploadObserver.setTransferListener(listener)
        val url = getUrl(fileName, extension)
        return url
    }

    private fun getTransferUtility(): TransferUtility {
        return TransferUtility.builder()
            .context(context)
            .defaultBucket(S3_BUCKET)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(s3Client)
            .build()
    }

    private fun getUrl(fileName: String, extension: String): String {
        val urlRequest = GeneratePresignedUrlRequest(
            S3_BUCKET,
            fileName
        )
        val url = s3Client.generatePresignedUrl(urlRequest)
        return if (extension == "mp4" || extension == "m3u8")
            "https://${url.host}${url.path}/VideoList/HLS${url.path}_360.m3u8" else
//            "https://${url.host}${url.path}/VideoList/MP4${url.path}.${extension}" else
            "https://${url.host}/${DIRECTORY}${url.path}.${extension}"
    }

}