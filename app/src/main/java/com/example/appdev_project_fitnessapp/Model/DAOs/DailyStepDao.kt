package com.example.appdev_project_fitnessapp.Model.DAOs

import androidx.room.*
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailyStepDao {
    @Query("SELECT * FROM daily_step_data WHERE date = :date")
    suspend fun getStepsForDate(date: Date): DailyStepData?

    @Query("SELECT * FROM daily_step_data WHERE date = :date")
    fun getStepsFlowForDate(date: Date): Flow<DailyStepData?>

    @Query("SELECT * FROM daily_step_data ORDER BY date DESC LIMIT :limit")
    fun getLastEntries(limit: Int): Flow<List<DailyStepData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(data: DailyStepData)

    @Query("SELECT SUM(steps) FROM daily_step_data")
    fun getTotalSteps(): Flow<Int?>
}
