package com.zipzaptaxi.live.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) return

        when (intent.action) {
            Intent.ACTION_SCREEN_OFF,
            AudioManager.ACTION_AUDIO_BECOMING_NOISY,
            "STOP_MEDIA_PLAYER" -> {
                stopMediaPlayer()
            }
            else -> {
                Log.e("NotificationReceiver", "Unknown action: ${intent.action}")
            }
        }
    }

    private fun stopMediaPlayer() {
        if (MyFirebaseMessagingService.mediaPlayer != null && MyFirebaseMessagingService.mediaPlayer!!.isPlaying) {
            MyFirebaseMessagingService.mediaPlayer!!.stop()
            MyFirebaseMessagingService.mediaPlayer!!.release()
            MyFirebaseMessagingService.mediaPlayer = null
        }
    }
}
