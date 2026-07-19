package com.example.appdev_project_fitnessapp.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.appdev_project_fitnessapp.Model.DAOs.DailyStepDao
import com.example.appdev_project_fitnessapp.Model.DAOs.DoneExerciseDao
import com.example.appdev_project_fitnessapp.Model.DAOs.ExerciseDao
import com.example.appdev_project_fitnessapp.Model.DAOs.ReminderDao
import com.example.appdev_project_fitnessapp.Model.DAOs.SetDao
import com.example.appdev_project_fitnessapp.Model.DAOs.TrainingSessionDao
import com.example.appdev_project_fitnessapp.Model.DAOs.TrainingTemplateDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import com.example.appdev_project_fitnessapp.Model.DataClasses.DoneExercise
import com.example.appdev_project_fitnessapp.Model.DataClasses.Exercise
import com.example.appdev_project_fitnessapp.Model.DataClasses.ExerciseSet
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingSession
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingTemplate

@Database(
        entities = [
        TrainingSession::class,
        DoneExercise::class,
        Exercise::class,
        ExerciseSet::class,
        TrainingTemplate::class,
        Reminder::class,
        DailyStepData::class
     ],
        version = 4,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun doneExerciseDao(): DoneExerciseDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SetDao
    abstract fun trainingTemplateDao(): TrainingTemplateDao
    abstract fun reminderDao(): ReminderDao
    abstract fun dailyStepDao(): DailyStepDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}