package com.example.appdev_project_fitnessapp.Model.Repo

import com.example.appdev_project_fitnessapp.Model.DAOs.DailyStepDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import kotlinx.coroutines.flow.Flow
import java.util.*

class StepRepository(private val dailyStepDao: DailyStepDao) {

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

    private fun normalizeDate(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}
