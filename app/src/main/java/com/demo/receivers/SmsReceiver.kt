package com.demo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.db.smsotpautofillkotlin.interfaces.SmsListener
import java.util.regex.Pattern

/**
 * Created by DB on 03-01-2018.
 */

class SmsReceiver: BroadcastReceiver() {

    @Suppress("DEPRECATION")
    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent!!.getExtras()

        val pdus = data?.get("pdus") as Array<Any>

        for (i in 0 until pdus.size) {

            val smsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)

            val sender = smsMessage.getDisplayOriginatingAddress()
            //Check the sender to filter messages which we require to read
            val pattern = Pattern.compile(OTP_SENDER_REGEX)
            val matcher = pattern.matcher(sender)

            while (matcher.find()) {
                val messageBody: String? = smsMessage.getMessageBody()
                //Pass the message text to interface
                if (messageBody != null) {
                    mListener!!.messageReceived(messageBody)
                }
            }

        }
    }

    companion object {
        val OTP_SENDER_REGEX: String? = "[0-9a-zA-Z]"
        //interface
        private var mListener: SmsListener? = null

        fun bindListener(listener: SmsListener) {
            mListener = listener
        }

    }
}