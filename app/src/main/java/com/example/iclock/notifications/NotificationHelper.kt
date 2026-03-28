package com.example.iclock.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.iclock.MainActivity
import com.example.iclock.R

object NotificationHelper {

    private const val CHANNEL_ID   = "glacier_alerts"
    private const val CHANNEL_NAME = "Glacier Alerts"
    private const val NOTIF_ID     = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weekly alerts for the Mer de Glace glacier"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun sendGlacierAlert(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "alert_detail")
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Glacier Alert — 2035")
            .setContentText("Mer de Glace will have lost 26% more coverage. Tap to see what you can do.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("By 2035, Mer de Glace will have an NDSI of 7.21 (WARNING). Temperatures will exceed 16°C during the Alpine summer. Tap to view the full report and find out what you can do.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIF_ID, notification)
    }
}