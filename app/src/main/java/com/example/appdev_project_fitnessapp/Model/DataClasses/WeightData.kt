package com.example.appdev_project_fitnessapp.Model.DataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weightKg: Double,
    val heightCm: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getBMI(): Double {
        val heightMeters = heightCm / 100.0
        return if (heightMeters > 0.0) weightKg / (heightMeters * heightMeters) else 0.0
    }
}
