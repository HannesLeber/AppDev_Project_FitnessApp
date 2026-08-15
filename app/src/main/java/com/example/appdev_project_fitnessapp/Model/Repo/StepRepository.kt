package com.example.appdev_project_fitnessapp.Model.Repo

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.appdev_project_fitnessapp.Helper.ExactReminderTimingManager
import com.example.appdev_project_fitnessapp.Helper.StepReminderWorker
import com.example.appdev_project_fitnessapp.Model.DAOs.DailyStepDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import kotlinx.coroutines.flow.Flow
import java.util.*

data class StepReminderSettings(
    val enabled: Boolean = false,
    val hour: Int = 18,
    val minute: Int = 0
)

class StepRepository(
    private val dailyStepDao: DailyStepDao,
    private val context: Context
) {

    fun getTodaySteps(date: Date): Flow<DailyStepData?> {
        return dailyStepDao.getStepsFlowForDate(normalizeDate(date))
    }

    suspend fun getStepsForDate(date: Date): DailyStepData? {
        return dailyStepDao.getStepsForDate(normalizeDate(date))
    }

    fun getLastSevenDays(): Flow<List<DailyStepData>> {
        return dailyStepDao.getLastEntries(7)
    }

    suspend fun updateSteps(steps: Int, target: Int) {
        val today = normalizeDate(Date())
        val existing = dailyStepDao.getStepsForDate(today)

        val distance = (steps * 0.75).toInt() // Schätzung: 0.75m pro Schritt
        val calories = (steps * 0.04).toInt() // Schätzung: 0.04 kcal pro Schritt

        val newData = DailyStepData(
            date = today,
            steps = steps,
            target = target,
            distanceMeters = distance,
            caloriesBurned = calories
        )
        dailyStepDao.insertOrUpdate(newData)
    }

    suspend fun updateTodayGoal(goal: Int) {
        val today = normalizeDate(Date())
        val existing = dailyStepDao.getStepsForDate(today)
        if (existing != null) {
            dailyStepDao.insertOrUpdate(existing.copy(target = goal))
        }
    }

    private fun normalizeDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun prefs() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getGoal(): Int {
        return prefs().getInt(KEY_DAILY_GOAL, DEFAULT_GOAL)
    }

    fun saveGoal(goal: Int) {
        prefs().edit().putInt(KEY_DAILY_GOAL, goal).apply()
    }

    fun getReminderSettings(): StepReminderSettings {
        val p = prefs()
        return StepReminderSettings(
            enabled = p.getBoolean(KEY_REMINDER_ENABLED, false),
            hour = p.getInt(KEY_REMINDER_HOUR, 18),
            minute = p.getInt(KEY_REMINDER_MINUTE, 0)
        )
    }

    fun saveReminderSettings(enabled: Boolean, hour: Int, minute: Int) {
        prefs().edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()
    }

    fun scheduleStepReminder(hour: Int, minute: Int) {
        val timingReminder = Reminder(
            title = "Schritte-Erinnerung",
            message = "",
            interval = 1L,
            unit = "DAYS",
            scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
            hour = hour,
            minute = minute
        )

        val request = OneTimeWorkRequestBuilder<StepReminderWorker>()
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
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancelStepReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    companion object {
        const val PREFS_NAME = "step_counter_prefs"
        const val KEY_DAILY_GOAL = "daily_goal"
        const val KEY_REMINDER_ENABLED = "reminder_enabled"
        const val KEY_REMINDER_HOUR = "reminder_hour"
        const val KEY_REMINDER_MINUTE = "reminder_minute"
        const val DEFAULT_GOAL = 10000
        const val WORK_NAME = "step_goal_reminder"
    }
}
