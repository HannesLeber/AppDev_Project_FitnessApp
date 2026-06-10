package com.example.appdev_project_fitnessapp.Model

import androidx.compose.runtime.mutableStateListOf
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TrainingSessionDao {
    @Query("SELECT * FROM trainingSession")
    suspend fun getAll(): List<TrainingSession>

    @Query("SELECT * FROM trainingSession WHERE id IN (:trainingSessionIds)")
    suspend fun loadAllByIds(trainingSessionIds: IntArray): List<TrainingSession>

    @Query("SELECT * FROM trainingSession WHERE id = :id")
    suspend fun findById(id: Int): TrainingSession

    @Query("SELECT * FROM trainingSession WHERE trainingName = :name")
    suspend fun findByName(name: String): List<TrainingSession>

    @Query("SELECT * FROM trainingSession WHERE trainingDate LIKE :date")
    suspend fun findByDate(date: Date): List<TrainingSession>

    @Insert
    suspend fun insert(trainingSession: TrainingSession)

    @Insert
    suspend fun insertAll(vararg trainingSessions: TrainingSession)

    @Delete
    suspend fun delete(trainingSession: TrainingSession)
}

@Dao
interface DoneExerciseDao {
    @Query("SELECT * FROM doneExercise")
    suspend fun getAll(): List<DoneExercise>

    @Query("SELECT * FROM doneExercise WHERE id IN (:doneExerciseIds)")
    suspend fun loadAllByIds(doneExerciseIds: IntArray): List<DoneExercise>


    @Query("SELECT * FROM doneExercise WHERE id = :id")
    suspend fun findById(id: Int): DoneExercise

    @Insert
    suspend fun insert(doneExercise: DoneExercise)

    @Insert
    suspend fun insertAll(vararg doneExercises: DoneExercise)

    @Delete
    suspend fun delete(doneExercise: DoneExercise)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM exercise WHERE id IN (:exerciseIds)")
    suspend fun loadAllByIds(exerciseIds: IntArray): List<Exercise>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun findById(id: Int): Exercise

    @Query("SELECT * FROM exercise WHERE exerciseName = :name")
    suspend fun findByName(name: String): List<Exercise>

    @Insert
    suspend fun insert(exercise: Exercise)

    @Insert
    suspend fun insertAll(vararg exercises: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

}

@Dao
interface SetDao {
    @Query("SELECT * FROM exerciseSet")
    suspend fun getAll(): List<ExerciseSet>

    @Query("SELECT * FROM exerciseSet WHERE id IN (:setIds)")
    suspend fun loadAllByIds(setIds: IntArray): List<ExerciseSet>

    @Query("SELECT * FROM exerciseSet WHERE id = :id")
    suspend fun findById(id: Int): ExerciseSet

    @Insert
    suspend fun insert(set: ExerciseSet)

    @Insert
    suspend fun insertAll(vararg sets: ExerciseSet)

    @Delete
    suspend fun delete(set: ExerciseSet)
}