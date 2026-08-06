package com.example.appdev_project_fitnessapp.Helper

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app.notifications.NotificationHelper
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder

class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        val id = inputData.getInt("id", 0)
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "Don't forget!"
        val interval = inputData.getLong("interval", 1L)
        val unit = inputData.getString("unit") ?: "MINUTES"
        val scheduleType = inputData.getString("scheduleType") ?: ExactReminderTimingManager.TYPE_INTERVAL
        val hour = inputData.getInt("hour", 8)
        val minute = inputData.getInt("minute", 0)
        val startHour = inputData.getInt("startHour", 8)
        val endHour = inputData.getInt("endHour", 22)
        val weekdays = inputData.getString("weekdays") ?: ""

        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            message = message,
            notificationId = id
        )

        val reminder = Reminder(
            id = id,
            title = title,
            message = message,
            interval = interval,
            unit = unit,
            scheduleType = scheduleType,
            hour = hour,
            minute = minute,
            startHour = startHour,
            endHour = endHour,
            weekdays = weekdays
        )

        val nextRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(
                ExactReminderTimingManager.calculateInitialDelayMillis(reminder),
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .setInputData(
                workDataOf(
                    "id" to id,
                    "title" to title,
                    "message" to message,
                    "interval" to interval,
                    "unit" to unit,
                    "scheduleType" to scheduleType,
                    "hour" to hour,
                    "minute" to minute,
                    "startHour" to startHour,
                    "endHour" to endHour,
                    "weekdays" to weekdays
                )
            )
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                "reminder_$id",
                ExistingWorkPolicy.REPLACE,
                nextRequest
            )

        return Result.success()
    }
}
