package com.demo.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.demo.R
import com.demo.activity.DashboardActivity
import com.demo.model.response.NotificationListShow
import com.demo.util.Prefs
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.io.IOException
import java.net.URL


class FcmService : FirebaseMessagingService() {

     var notificationBeanList =ArrayList<NotificationBean>()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("device token", token)
        Prefs.init().deviceToken=token
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("messagereceive", "receive" +remoteMessage.notification?.body)

        if(Prefs.init().notificationAll && Prefs.init().profileData!=null) {
            createNotificationChannel(remoteMessage.notification?.body)
        }
    }

    fun getMessageForShow(notificationBean:NotificationBean):String{

        var text:String
        if (notificationBean.notificationType!=null){
            if(notificationBean.notificationType=="like"){
                text=notificationBean.user?.userName +" "+applicationContext.resources.getString(R.string.liked_post)
            }else if(notificationBean.notificationType=="comment"){
                text=notificationBean.user?.userName +" "+applicationContext.resources.getString(R.string.comment_on_post)

            }else{
                text=notificationBean.user?.userName +" "+applicationContext.resources.getString(R.string.started_follwg)
            }

        }else{
            text=notificationBean.msg.toString()
        }
        return text
    }

    private fun createNotificationChannel(remoteMessage: String?) {

      /* val notificationContent = remoteMessage?.data


        Log.e("message receive", "receive "+remoteMessage?.data)

       // val answer = JSONObject(remoteMessage?.data.toString())

        var gson: Gson
        gson = Gson()
        val notificationBean = gson.run { fromJson(remoteMessage?.data?.get("responsePacket"), NotificationBean::class.java) }



        if (Prefs.init().notificationListShow?.notificationObject!=null){
            notificationBeanList=Prefs.init().notificationListShow?.notificationObject as ArrayList<NotificationBean>
        }
        notificationBeanList.add(notificationBean)


        var notificationListShow= NotificationListShow()
        notificationListShow.notificationObject=notificationBeanList


        Prefs.init().notificationListShow=notificationListShow*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this


                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager!!.createNotificationChannel(channel)


        }

        val notifyIntent = Intent(this, DashboardActivity::class.java)
        notifyIntent.putExtra("iSNotification",true)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      //  notifyIntent.putExtra(AppIntentExtraKeys.NOTIFICATION_TYPE, notificationBean.notificationType)
//        val notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)



       /* if (notificationBean.notificationType!=null){
            if(notificationBean.notificationType=="like"){
                text=applicationContext.resources.getString(R.string.liked_post)
            }else if(notificationBean.notificationType=="comment"){
                text=applicationContext.resources.getString(R.string.comment_on_post)

            }else{
                text=applicationContext.resources.getString(R.string.started_follwg)
            }

        }else{
            text=notificationBean.msg.toString()
        }*/



      /*  var bmp: Bitmap? = null
        try {
            val `in` = URL(notificationBean.user?.profileImage).openStream()
            bmp = BitmapFactory.decodeStream(`in`)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
       /* val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"
        Prefs.init().notificationData=Prefs.init().notificationData+", "+notificationBean.user?.userName+" "+text
*/
        val mBuilder = NotificationCompat.Builder(this, getString(R.string.channel_id))
            .setSmallIcon(R.drawable.notif_icon)
            .setContentTitle("Nuna")
            .setContentText(remoteMessage)
//            .setContentIntent(notifyPendingIntent)
            .setAutoCancel(true)
           // .setLargeIcon(bmp)
//            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setDefaults(Notification.DEFAULT_ALL)
//            .setColor(ContextCompat.getColor(baseContext,R.color.colorPrimary))
        val inboxStyle: NotificationCompat.InboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle("")
        //inboxStyle.setSummaryText("mehulrughani@gmail.com")

      /*  for (mesg in notificationBeanList) {
            inboxStyle.addLine(getMessageForShow(mesg))
        }


        currentNotificationID = 500
        mBuilder.setStyle(inboxStyle)
            .setPriority(1)
*/

        /* to remove notification after click*/
       // mBuilder.flags = mBuilder.flags or Notification.FLAG_AUTO_CANCEL

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(21, mBuilder.build())
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


