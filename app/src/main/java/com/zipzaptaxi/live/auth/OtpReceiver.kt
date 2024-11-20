package com.zipzaptaxi.live.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import java.lang.ref.WeakReference
import java.util.regex.Pattern

class OtpReceiver(activity: VerifyOtpActivity) : BroadcastReceiver() {

    private val activityRef = WeakReference(activity)

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>
                val messages: Array<SmsMessage?> = arrayOfNulls(pdus.size)
                for (i in pdus.indices) {
                    messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    val messageBody = messages[i]?.messageBody
                    messageBody?.let {
                        extractAndFillOTP(context, it) }
                }
            }
        }
    }

    private fun extractAndFillOTP(context: Context?, message: String) {
        val otpPattern = Pattern.compile("\\b\\d{4}\\b")
        val matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            val otp = matcher.group(0)
            otp?.let {
                // Use the OTP as needed
                Log.d("SmsReceiver", "Extracted OTP: $otp")
                fillOtpInEditText(otp)
            }
        }
    }

    private fun fillOtpInEditText(otp: String) {
        val activity = activityRef.get()
        activity?.runOnUiThread {
            activity.otpEditText.setText(otp)
        }
    }
}
