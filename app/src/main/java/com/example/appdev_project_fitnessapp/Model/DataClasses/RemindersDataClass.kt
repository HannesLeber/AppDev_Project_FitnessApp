package com.example.appdev_project_fitnessapp.Model.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val message: String,

    // WATER, SUPPLEMENTS, TRAINING, GENERAL
    val category: String = "GENERAL",

    // Example: 30, 2, 1, 7 etc.
    val interval: Long,

    // MINUTES, HOURS, DAYS
    val unit: String,

    // INTERVAL, DAILY_AT_TIME, WINDOWED_INTERVAL
    val scheduleType: String = "INTERVAL",

    val hour: Int = 8,

    val minute: Int = 0,

    val startHour: Int = 8,

    val endHour: Int = 22,

    // Empty - every day. Values: MONDAY,TUESDAY,...
    val weekdays: String = "",

    val enabled: Boolean = true
)
