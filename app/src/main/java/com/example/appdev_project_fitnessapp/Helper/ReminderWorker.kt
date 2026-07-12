package com.example.appdev_project_fitnessapp.Helper

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.app.notifications.NotificationHelper

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val title = inputData.getString("title") ?: "Reminder"

        val message = inputData.getString("message") ?: "Don't forget!"

        val notificationId = inputData.getInt("id", 0)

        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            message = message,
            notificationId = notificationId
        )

        return Result.success()
    }
}