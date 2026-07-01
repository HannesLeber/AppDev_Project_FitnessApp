package com.example.appdev_project_fitnessapp.Model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

//TODO: Update-Funktionen schreiben! Aber nicht löschen und dann neu hinzufügen, sonst Problem mit den IDs
@Dao
interface TrainingSessionDao {
    @Query("SELECT * FROM TrainingSession ORDER BY id DESC")
    suspend fun getAll(): List<TrainingSession>

    @Query("SELECT * FROM TrainingSession WHERE id IN (:trainingSessionIds)")
    suspend fun loadAllByIds(trainingSessionIds: IntArray): List<TrainingSession>

    @Query("SELECT * FROM TrainingSession WHERE id = :id")
    suspend fun findById(id: Int): TrainingSession

    @Query("SELECT * FROM TrainingSession WHERE trainingName = :name")
    suspend fun findByName(name: String): List<TrainingSession>

    @Query("SELECT * FROM TrainingSession WHERE trainingDate = :date")
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
    @Query("SELECT * FROM DoneExercise ORDER BY id DESC")
    suspend fun getAll(): List<DoneExercise>

    @Query("SELECT * FROM DoneExercise WHERE id IN (:doneExerciseIds) ")
    suspend fun loadAllByIds(doneExerciseIds: IntArray): List<DoneExercise>

    @Query("SELECT * FROM DoneExercise WHERE id = :id")
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
    @Query("SELECT * FROM Exercise ORDER BY id DESC")
    suspend fun getAll(): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE id IN (:exerciseIds)")
    suspend fun loadAllByIds(exerciseIds: IntArray): List<Exercise>

    @Query("SELECT * FROM Exercise WHERE id = :id")
    suspend fun findById(id: Int): Exercise

    @Query("SELECT * FROM Exercise WHERE exerciseName = :name")
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
    @Query("SELECT * FROM ExerciseSet")
    suspend fun getAll(): List<ExerciseSet>

    @Query("SELECT * FROM ExerciseSet WHERE id IN (:setIds)")
    suspend fun loadAllByIds(setIds: IntArray): List<ExerciseSet>

    @Query("SELECT * FROM ExerciseSet WHERE id = :id")
    suspend fun findById(id: Int): ExerciseSet

    @Insert
    suspend fun insert(set: ExerciseSet)

    @Insert
    suspend fun insertAll(vararg sets: ExerciseSet)

    @Delete
    suspend fun delete(set: ExerciseSet)
}


@Dao
interface TrainingTemplateDao {
    @Query("SELECT * FROM TrainingTemplate")
    suspend fun getAll(): List<TrainingTemplate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: TrainingTemplate): Long

    @Delete
    suspend fun delete(template: TrainingTemplate)
}
