package com.example.appdev_project_fitnessapp.Helper

import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.DayOfWeek
import java.time.temporal.ChronoUnit

object ExactReminderTimingManager {
    const val TYPE_INTERVAL = "INTERVAL"
    const val TYPE_DAILY_AT_TIME = "DAILY_AT_TIME"
    const val TYPE_WINDOWED_INTERVAL = "WINDOWED_INTERVAL"

    fun calculateInitialDelayMillis(reminder: Reminder): Long {
        val now = LocalDateTime.now()
        return when (reminder.scheduleType) {
            TYPE_INTERVAL -> calculateIntervalDelay(reminder)
            TYPE_DAILY_AT_TIME -> calculateDailyDelay(reminder, now)
            TYPE_WINDOWED_INTERVAL -> calculateWindowedDelay(reminder, now)
            else -> calculateIntervalDelay(reminder)
        }
    }

    private fun calculateIntervalDelay(reminder: Reminder): Long {
        val intervalMs = when (reminder.unit) {
            "MINUTES" -> reminder.interval * 60 * 1000
            "HOURS" -> reminder.interval * 60 * 60 * 1000
            "DAYS" -> reminder.interval * 24 * 60 * 60 * 1000
            else -> reminder.interval * 60 * 60 * 1000
        }
        return intervalMs
    }

    private fun calculateDailyDelay(reminder: Reminder, now: LocalDateTime): Long {
        var nextRun = now.withHour(reminder.hour).withMinute(reminder.minute).withSecond(0).withNano(0)
        
        if (nextRun.isBefore(now)) {
            nextRun = nextRun.plusDays(1)
        }

        if (reminder.weekdays.isNotBlank()) {
            try {
                val allowedDays = reminder.weekdays.split(",")
                    .filter { it.isNotBlank() }
                    .map { DayOfWeek.valueOf(it.trim().uppercase()) }
                    .toSet()
                
                if (allowedDays.isNotEmpty()) {
                    while (!allowedDays.contains(nextRun.dayOfWeek)) {
                        nextRun = nextRun.plusDays(1)
                    }
                }
            } catch (e: Exception) {
                // Fallback if weekdays string is malformed
            }
        }

        return Duration.between(now, nextRun).toMillis()
    }

    private fun calculateWindowedDelay(reminder: Reminder, now: LocalDateTime): Long {
        val intervalMs = calculateIntervalDelay(reminder)
        var nextRun = now.plus(intervalMs, ChronoUnit.MILLIS)
        
        val hour = nextRun.hour
        if (hour < reminder.startHour) {
            nextRun = nextRun.withHour(reminder.startHour).withMinute(0).withSecond(0).withNano(0)
        } else if (hour >= reminder.endHour) {
            nextRun = nextRun.plusDays(1).withHour(reminder.startHour).withMinute(0).withSecond(0).withNano(0)
        }
        
        return Duration.between(now, nextRun).toMillis()
    }

    fun timingDescription(reminder: Reminder): String {
        return when (reminder.scheduleType) {
            TYPE_INTERVAL -> "Every ${reminder.interval} ${reminder.unit.lowercase()}"
            TYPE_DAILY_AT_TIME -> {
                val time = String.format("%02d:%02d", reminder.hour, reminder.minute)
                if (reminder.weekdays.isBlank()) "Daily at $time"
                else "At $time on ${reminder.weekdays.lowercase().replace(",", ", ")}"
            }
            TYPE_WINDOWED_INTERVAL -> "Every ${reminder.interval} ${reminder.unit.lowercase()} between ${reminder.startHour}:00 and ${reminder.endHour}:00"
            else -> "Scheduled"
        }
    }

    fun defaultTrainingWeekdays(): String {
        return "MONDAY,WEDNESDAY,FRIDAY"
    }
}
