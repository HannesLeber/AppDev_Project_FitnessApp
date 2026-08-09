package com.example.appdev_project_fitnessapp.Model.DAOs

import androidx.room.*
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long
    @Update
    suspend fun update(reminder: Reminder)
    @Delete
    suspend fun delete(reminder: Reminder)
    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<Reminder>

    @Query("SELECT * FROM reminders WHERE enabled = 1")
    suspend fun getEnabled(): List<Reminder>
    @Query("SELECT * FROM reminders WHERE title = :title LIMIT 1")
    suspend fun getByTitle(title: String): Reminder?
}
