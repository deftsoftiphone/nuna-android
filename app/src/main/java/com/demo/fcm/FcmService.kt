package com.demo.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.model.response.baseResponse.FCMNotificationResponse
import com.demo.util.Prefs
import com.demo.util.getBitmapFromURL
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus


class FcmService : FirebaseMessagingService() {

    var notificationBeanList = ArrayList<NotificationBean>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("device token", token)
        Prefs.init().deviceToken = token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (Prefs.init().currentUser != null) {
            createNotificationChannel(
                remoteMessage,
                remoteMessage.notification?.title
            )
            EventBus.getDefault().postSticky(Bundle())
        }
    }

    fun getMessageForShow(notificationBean: NotificationBean): String {

        var text: String
        if (notificationBean.notificationType != null) {
            if (notificationBean.notificationType == "like") {
                text =
                    notificationBean.user?.userName + " " + applicationContext.resources.getString(R.string.liked_post)
            } else if (notificationBean.notificationType == "comment") {
                text =
                    notificationBean.user?.userName + " " + applicationContext.resources.getString(R.string.comment_on_post)
            } else {
                text =
                    notificationBean.user?.userName + " " + applicationContext.resources.getString(R.string.started_follwg)
            }

        } else {
            text = notificationBean.msg.toString()
        }
        return text
    }


    private fun createNotificationChannel(
        remoteMessage: RemoteMessage?,
        title: String?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.new_channel_name)
            val description = getString(R.string.new_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }

        val notificationData = Gson().fromJson(
            remoteMessage?.data?.toString(), FCMNotificationResponse::class.java
        )

        val notifyIntent = Intent(this, DashboardActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        notifyIntent.putExtra("iSNotification", true)
            .putExtra(
                getString(R.string.key_notification_type), notificationData.data?.type
            )
            .putExtra(getString(R.string.key_activity_id), notificationData.data?.activityId)
            .putExtra(getString(R.string.key_user_id), notificationData.data?.userId)
            .putExtra(getString(R.string.key_post_id), notificationData.data?.postId)

        val notificationCode = System.currentTimeMillis().toInt()
        val pIntent: PendingIntent = PendingIntent.getActivity(
            this, notificationCode, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


        val mBuilder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setContentIntent(pIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pIntent)
            .setSmallIcon(R.drawable.notification_icon)

        if (!TextUtils.isEmpty(notificationData.data?.media)) {
            val bigPictureBitmap = getBitmapFromURL(notificationData.data?.media)
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
                .setBigContentTitle(title)
                .setSummaryText(notificationData.data?.text)
//                .bigPicture(bigPictureBitmap)
//                .bigLargeIcon(bigPictureBitmap)

            mBuilder
//                .setStyle(bigPictureStyle)
                .setContentTitle(notificationData.data?.title)
                .setLargeIcon(bigPictureBitmap)
                .setContentText(notificationData.data?.text)
        } else
            mBuilder
                .setContentTitle(notificationData.data?.title)
                .setContentText(notificationData.data?.text)
                .setColor(getColor(R.color.transparent))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)

        val inboxStyle: NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("")
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationCode, mBuilder.build())
    }

    private val notificationManager: NotificationManager? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var combinedNotificationCounter = 0
    private var currentNotificationID = 0

/*    private fun setDataForCombinedNotification() {
        combinedNotificationCounter++
        notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(icon)
            .setContentTitle(notificationTitle)
            .setGroup("group_emails")
            .setGroupSummary(true)
            .setContentText(combinedNotificationCounter.toString() + " new messages")
        val inboxStyle: NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(notificationTitle)
        inboxStyle.setSummaryText("mehulrughani@gmail.com")
        for (i in 0 until combinedNotificationCounter) {
            inboxStyle.addLine("This is Test$i")
        }
        currentNotificationID = 500
        notificationBuilder.setStyle(inboxStyle)

    }*/


}


