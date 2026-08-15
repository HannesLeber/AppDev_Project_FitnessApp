package com.example.appdev_project_fitnessapp.Model.Repo

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.appdev_project_fitnessapp.Helper.ExactReminderTimingManager
import com.example.appdev_project_fitnessapp.Helper.ReminderWorker
import com.example.appdev_project_fitnessapp.Model.DAOs.ReminderDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder

class ReminderRepository(
    private val dao: ReminderDao,
    private val context: Context
) {

    suspend fun insert(reminder: Reminder): Reminder {
        val existing = dao.getByTitle(reminder.title)

        return if (existing == null) {
            val id = dao.insert(reminder).toInt()
            reminder.copy(id = id)
        } else {
            val updated = reminder.copy(id = existing.id)
            cancelReminder(updated)
            dao.update(updated)
            updated
        }
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

    suspend fun getByTitle(title: String): Reminder? {
        return dao.getByTitle(title)
    }

    fun scheduleReminder(reminder: Reminder) {

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(
                ExactReminderTimingManager.calculateDelayMillis(reminder),
                java.util.concurrent.TimeUnit.MILLISECONDS
            )
            .setInputData(
                workDataOf(
                    "id" to reminder.id,
                    "title" to reminder.title,
                    "message" to reminder.message,
                    "interval" to reminder.interval,
                    "unit" to reminder.unit,
                    "scheduleType" to reminder.scheduleType,
                    "hour" to reminder.hour,
                    "minute" to reminder.minute,
                    "startHour" to reminder.startHour,
                    "endHour" to reminder.endHour,
                    "weekdays" to reminder.weekdays
                )
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "reminder_${reminder.id}",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }

    fun cancelReminder(reminder: Reminder) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("reminder_${reminder.id}")
    }
}
