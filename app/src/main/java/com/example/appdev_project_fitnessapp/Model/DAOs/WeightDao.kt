package com.example.appdev_project_fitnessapp.Model.DAOs

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.appdev_project_fitnessapp.Model.DataClasses.WeightEntry

@Dao
interface WeightDao {
    @Insert
    suspend fun insert(entry: WeightEntry)

    @Delete
    suspend fun delete(entry: WeightEntry)

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    suspend fun getAll(): List<WeightEntry>
}
