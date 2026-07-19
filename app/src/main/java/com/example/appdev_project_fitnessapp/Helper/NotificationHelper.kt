package com.example.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.appdev_project_fitnessapp.MainActivity

object NotificationHelper {

    const val CHANNEL_ID = "reminder_channel"
    const val STEP_CHANNEL_ID = "step_counter_channel"

    fun createNotificationChannel(context: Context) {

        val reminderChannel = NotificationChannel(
            CHANNEL_ID,
            "Reminder Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for reminder reminders"
        }

        val stepChannel = NotificationChannel(
            STEP_CHANNEL_ID,
            "Schrittzähler",
            NotificationManager.IMPORTANCE_LOW // Low priority for silent background updates
        ).apply {
            description = "Zeigt den aktuellen Schrittestatus an"
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(reminderChannel)
        manager.createNotificationChannel(stepChannel)
        Log.d("Notification", "Created")

    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int
    ) {

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Notidication", "No permission")
            return
        }

        NotificationManagerCompat
            .from(context)
            .notify(notificationId, notification)
    }
}