package com.example.appdev_project_fitnessapp.Helper

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.app.notifications.NotificationHelper
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import com.example.appdev_project_fitnessapp.Model.Repo.StepRepository
import java.util.Date

class StepReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val hour = inputData.getInt("hour", 18)
        val minute = inputData.getInt("minute", 0)

        val repository = StepRepository(AppDatabase.getDatabase(context).dailyStepDao(), context)
        val reminderSettings = repository.getReminderSettings()

        if (!reminderSettings.enabled) {
            return Result.success()
        }

        val goal = repository.getGoal()
        val steps = repository.getStepsForDate(Date())?.steps ?: 0

        if (steps < goal) {
            NotificationHelper.showNotification(
                context = context,
                title = "Noch Schritte offen!",
                message = "Du hast heute erst $steps von $goal Schritten geschafft. Noch ${goal - steps} bis zum Ziel!",
                notificationId = NOTIFICATION_ID
            )
        }

        rescheduleForNextDay(hour, minute)

        return Result.success()
    }

    private fun rescheduleForNextDay(hour: Int, minute: Int) {
        val timingReminder = Reminder(
            title = "Schritte-Erinnerung",
            message = "",
            interval = 1L,
            unit = "DAYS",
            scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
            hour = hour,
            minute = minute
        )

        val nextRequest = OneTimeWorkRequestBuilder<StepReminderWorker>()
            .setInitialDelay(
                ExactReminderTimingManager.calculateDelayMillis(timingReminder),
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .setInputData(
                workDataOf(
                    "hour" to hour,
                    "minute" to minute
                )
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                StepRepository.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                nextRequest
            )
    }

    companion object {
        private const val NOTIFICATION_ID = 2001
    }
}
