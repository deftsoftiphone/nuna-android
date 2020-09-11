package com.demo.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.google.android.gms.common.internal.service.Common


interface SmsBroadcastReceiverListener {
    fun onSuccess(message: String?)
    fun onFailure()
}

class SMSListener : BroadcastReceiver() {


    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val data = intent.extras
        var pdus: Array<Any?>? = arrayOfNulls(0)
        if (data != null) {
            pdus =
                data["pdus"] as Array<Any?>? // the pdus key will contain the newly received SMS
        }
        if (pdus != null) {
            for (pdu in pdus) { // loop through and pick up the SMS of interest
                val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdu as ByteArray?)

                // your custom logic to filter and extract the OTP from relevant SMS - with regex or any other way.
                if (mListener != null) mListener!!.onSuccess(
                    smsMessage.displayMessageBody
                )
                break
            }
        }
    }

    companion object {
        private var mListener // this listener will do the magic of throwing the extracted OTP to all the bound views.
                : SmsBroadcastReceiverListener? = null

        fun bindListener(listener: SmsBroadcastReceiverListener) {
            mListener = listener
        }

        fun unbindListener() {
            mListener = null
        }
    }

}