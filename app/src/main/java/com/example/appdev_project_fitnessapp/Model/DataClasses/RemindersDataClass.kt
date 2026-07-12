package com.example.appdev_project_fitnessapp.Model.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val message: String,

    // Example: 30, 2, 1, 7 etc.
    val interval: Long,

    // MINUTES, HOURS, DAYS
    val unit: String,

    val enabled: Boolean = true
)