package com.example.appdev_project_fitnessapp.Helper

import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object ExactReminderTimingManager {

    const val TYPE_INTERVAL = "INTERVAL"
    const val TYPE_DAILY_AT_TIME = "DAILY_AT_TIME"
    const val TYPE_WINDOWED_INTERVAL = "WINDOWED_INTERVAL"

    fun calculateInitialDelayMillis(reminder: Reminder): Long {
        return calculateNextDelayMillis(reminder, LocalDateTime.now())
    }

    fun calculateNextDelayMillis(reminder: Reminder, now: LocalDateTime): Long {
        val nextTime = when (reminder.scheduleType) {
            TYPE_DAILY_AT_TIME -> nextDailyTime(now, reminder)
            TYPE_WINDOWED_INTERVAL -> nextWindowedIntervalTime(now, reminder)
            else -> now.plusMinutes(reminder.intervalInMinutes())
        }

        return Duration.between(now, nextTime).toMillis().coerceAtLeast(1L)
    }

    fun timingDescription(reminder: Reminder): String {
        val days = reminder.weekdayDescription()
        val timing = when (reminder.scheduleType) {
            TYPE_DAILY_AT_TIME -> "Daily at ${reminder.hour.timePart()}:${reminder.minute.timePart()}"
            TYPE_WINDOWED_INTERVAL -> {
                "Every ${reminder.interval} ${reminder.unit.lowercase()} from ${reminder.startHour.timePart()}:00 to ${reminder.endHour.timePart()}:00"
            }
            else -> "Every ${reminder.interval} ${reminder.unit}"
        }

        return if (days.isBlank()) timing else "$timing on $days"
    }

    fun defaultTrainingWeekdays(): String {
        return listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.FRIDAY
        ).joinToString(",") { it.name }
    }

    private fun nextDailyTime(now: LocalDateTime, reminder: Reminder): LocalDateTime {
        val targetTime = LocalTime.of(reminder.hour.coerceIn(0, 23), reminder.minute.coerceIn(0, 59))
        var candidate = now.toLocalDate().atTime(targetTime)
        val weekdays = reminder.selectedWeekdays()

        repeat(8) {
            if (candidate.isAfter(now) && candidate.dayMatches(weekdays)) {
                return candidate
            }
            candidate = candidate.plusDays(1)
        }

        return candidate
    }

    private fun nextWindowedIntervalTime(now: LocalDateTime, reminder: Reminder): LocalDateTime {
        val startHour = reminder.startHour.coerceIn(0, 23)
        val endHour = reminder.endHour.coerceIn(startHour, 23)
        val weekdays = reminder.selectedWeekdays()
        val intervalMinutes = reminder.intervalInMinutes().coerceAtLeast(1L)
        var dayStart = now.toLocalDate().atTime(startHour, 0)

        repeat(8) {
            val dayEnd = dayStart.toLocalDate().atTime(endHour, 0)
            if (dayStart.dayMatches(weekdays)) {
                if (now.isBefore(dayStart)) return dayStart
                if (now.isBefore(dayEnd)) {
                    var nextTime = dayStart
                    while (!nextTime.isAfter(now)) {
                        nextTime = nextTime.plusMinutes(intervalMinutes)
                    }

                    if (!nextTime.isAfter(dayEnd)) return nextTime
                }
            }
            dayStart = dayStart.plusDays(1)
        }

        return dayStart
    }

    private fun Reminder.intervalInMinutes(): Long {
        return when (unit) {
            "HOURS" -> interval * 60
            "DAYS" -> interval * 24 * 60
            else -> interval
        }.coerceAtLeast(1L)
    }

    private fun Reminder.selectedWeekdays(): Set<DayOfWeek> {
        if (weekdays.isBlank()) return emptySet()

        return weekdays.split(",")
            .mapNotNull { day ->
                runCatching { DayOfWeek.valueOf(day.trim()) }.getOrNull()
            }
            .toSet()
    }

    private fun Reminder.weekdayDescription(): String {
        val selected = selectedWeekdays()
        if (selected.isEmpty()) return ""

        return DayOfWeek.entries
            .filter { it in selected }
            .joinToString(", ") { day -> day.name.take(3).lowercase().replaceFirstChar { it.uppercase() } }
    }

    private fun LocalDateTime.dayMatches(weekdays: Set<DayOfWeek>): Boolean {
        return weekdays.isEmpty() || dayOfWeek in weekdays
    }

    private fun Int.timePart(): String = toString().padStart(2, '0')
}
