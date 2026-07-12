package com.example.appdev_project_fitnessapp.Model.Repo

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.appdev_project_fitnessapp.Helper.ReminderWorker
import com.example.appdev_project_fitnessapp.Model.DAOs.ReminderDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import java.util.concurrent.TimeUnit

class ReminderRepository(
    private val dao: ReminderDao,
    private val context: Context
) {

    suspend fun insert(reminder: Reminder) {
        dao.insert(reminder)
    }

    suspend fun update(reminder: Reminder) {
        dao.update(reminder)
    }

    suspend fun delete(reminder: Reminder) {
        dao.delete(reminder)
        cancelReminder(reminder)
    }

    suspend fun getAll(): List<Reminder> {
        return dao.getAll()
    }

    fun scheduleReminder(reminder: Reminder) {

        val inputData = workDataOf(
            "id" to reminder.id,
            "title" to reminder.title,
            "message" to reminder.message
        )

        when (reminder.unit) {

            "MINUTES" -> {
                val request =
                    OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(
                            reminder.interval,
                            TimeUnit.MINUTES
                        )
                        .setInputData(inputData)
                        .build()

                WorkManager.Companion.getInstance(context)
                    .enqueue(request)
            }

            "HOURS" -> {
                val request =
                    PeriodicWorkRequestBuilder<ReminderWorker>(
                        reminder.interval,
                        TimeUnit.HOURS
                    )
                        .setInputData(inputData)
                        .build()

                WorkManager.Companion.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        "reminder_${reminder.id}",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                    )
            }

            "DAYS" -> {
                val request =
                    PeriodicWorkRequestBuilder<ReminderWorker>(
                        reminder.interval,
                        TimeUnit.DAYS
                    )
                        .setInputData(inputData)
                        .build()
                WorkManager.Companion.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        "reminder_${reminder.id}",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                    )
            }
        }
    }

    fun cancelReminder(reminder: Reminder) {
        WorkManager.Companion.getInstance(context)
            .cancelUniqueWork("reminder_${reminder.id}")
    }
}