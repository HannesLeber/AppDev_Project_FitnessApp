package com.example.appdev_project_fitnessapp.Model.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_step_data")
data class DailyStepData(
    @PrimaryKey
    val date: Date, // Normalisiert auf Mitternacht
    val steps: Int,
    val target: Int,
    val distanceMeters: Int,
    val caloriesBurned: Int
)
