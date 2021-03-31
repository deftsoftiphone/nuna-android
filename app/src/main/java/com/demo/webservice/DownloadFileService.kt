package com.demo.webservice

import android.app.DownloadManager
import android.app.IntentService
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File


class DownloadFileService : IntentService("sfsdf"), KodeinAware {


    private lateinit var fileName: String
    override val kodein: Kodein by closestKodein()
    private val apiService: APIService by instance()
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private var totalFileSize: Int = 0

    override fun onHandleIntent(intent: Intent?) {
//        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        notificationBuilder = NotificationCompat.Builder(this)
//            .setSmallIcon(R.mipmap.ic_launcher)
//            .setContentTitle("Download")
//            .setContentText("Downloading File")
//            .setAutoCancel(true);
//        notificationManager.notify(0, notificationBuilder.build());

        GlobalScope.launch(Dispatchers.IO) {
            intent?.let {
                fileName = it.getStringExtra("FILE_PATH")!!
                downloadFile2(it.getStringExtra("FILE_URL")!!)
            }
        }
    }

    private fun downloadFile2(url: String) {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path,
            fileName
        )

        if (file.exists()) {
            file.delete()
        }
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("Media file is downloading..")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setDestinationUri(Uri.fromFile(file))
        val downloadManager: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val downloadID: Long = downloadManager.enqueue(request)
    }

}