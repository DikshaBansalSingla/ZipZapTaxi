package com.zipzaptaxi.live.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.SCREEN_BRIGHT_WAKE_LOCK
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.zipzaptaxi.live.R
import com.zipzaptaxi.live.bookings.BookingDetail
import com.zipzaptaxi.live.home.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "FireBasePush"
    private var CHANNEL_ID = "Zipzap Taxi"
    private var title = ""
    private var message = ""
    private var type = ""
    private lateinit var mediaPlayerBroadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter

    lateinit var notificationChannel: NotificationChannel
    private val CHANNEL_ONE_NAME = "Channel One"

    override fun onCreate() {
        super.onCreate()
        mediaPlayerBroadcastReceiver = NotificationBroadcastReceiver()
        intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
            addAction("STOP_MEDIA_PLAYER")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mediaPlayerBroadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mediaPlayerBroadcastReceiver)
    }

    override fun onNewToken(refeshToken: String) {
        super.onNewToken(refeshToken)
        Log.e(TAG, "Refreshed token: $refeshToken")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
                lightColor = Color.RED
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
        }*/

        Log.e(TAG, "Notification: ${remoteMessage.data}")

        try {
            remoteMessage.data?.let {
                val title = it["title"].toString()
                val message = it["message"].toString()
                type = it["notification_type"].toString()

                val notificationIntent = if (type == "booking") {
                   /* Intent(this, MainActivity::class.java).apply {
                        putExtra("id", it["booking_id"].toString())
                    }*/
                    Intent(this, MainActivity::class.java)
                } else {
                    Intent(this, MainActivity::class.java)
                }
                makePush(title, message, notificationIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun makePush(title: String, message: String, intent: Intent?) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val channelId = CHANNEL_ID
        val defaultSoundUri: Uri = if (type == "booking") {
            Uri.parse("android.resource://" + packageName + "/" + R.raw.notification)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val notificationId = System.currentTimeMillis().toInt()
        val dismissIntent = Intent(this, NotificationBroadcastReceiver::class.java).apply {
            putExtra("notification_id", notificationId)
            action = "STOP_MEDIA_PLAYER"
        }
        val dismissPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(applicationContext, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT)
        } else {
            PendingIntent.getBroadcast(applicationContext, 0, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(notificationIcon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.app_logo))
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 100, 200, 300))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                lightColor = Color.MAGENTA
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, notificationBuilder.build())

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                stopMediaPlayer()
            }
        }

        val result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                    setDataSource(applicationContext, defaultSoundUri)
                    prepare()
                    start()
                    setOnCompletionListener {
                        stopMediaPlayer()
                        audioManager.abandonAudioFocus(audioFocusChangeListener)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.newWakeLock(SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "AppName:tag").acquire(1000)
    }

    private fun stopMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
                mediaPlayer = null
            }
        }
    }

    private val notificationIcon: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) R.mipmap.ic_logo else R.mipmap.ic_launcher_round

    companion object {
        var notificationManager: NotificationManager? = null
        var mediaPlayer: MediaPlayer? = null
    }
}
