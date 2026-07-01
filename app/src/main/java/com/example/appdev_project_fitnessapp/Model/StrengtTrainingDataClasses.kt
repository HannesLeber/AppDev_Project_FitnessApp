package com.example.appdev_project_fitnessapp.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class TrainingSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "trainingName") var name: String?,
    @ColumnInfo(name = "trainingExercises") var doneExercises: List<Int>,
    @ColumnInfo(name = "trainingDate") var date: Date
)

@Entity
data class DoneExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "exerciseID") var exerciseID: Int,
    @ColumnInfo(name = "exerciseSets") var sets: List<Int>
)

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "exerciseName") var name: String,
    @ColumnInfo(name = "prSetID") var prSetID: Int?,
    @ColumnInfo(name = "doneExercises") var doneExercises: List<Int>
)

@Entity
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "reps") var reps: Int,
    @ColumnInfo(name = "weight") var weight: Double,
    @ColumnInfo(name = "warmUpSet") var warmupSet: Boolean
)

@Entity
data class TrainingTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "templateName") var name: String,
    @ColumnInfo(name = "exerciseIds") var exerciseIds: List<Int>
)
