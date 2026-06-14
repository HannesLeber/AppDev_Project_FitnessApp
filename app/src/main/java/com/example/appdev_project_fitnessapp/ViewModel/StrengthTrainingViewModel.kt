package com.example.appdev_project_fitnessapp.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.DoneExercise
import com.example.appdev_project_fitnessapp.Model.DoneExerciseDao
import com.example.appdev_project_fitnessapp.Model.Exercise
import com.example.appdev_project_fitnessapp.Model.ExerciseDao
import com.example.appdev_project_fitnessapp.Model.ExerciseSet
import com.example.appdev_project_fitnessapp.Model.SetDao
import com.example.appdev_project_fitnessapp.Model.TrainingSession
import com.example.appdev_project_fitnessapp.Model.TrainingSessionDao
import kotlinx.coroutines.launch

class StrengthTrainingViewModel(
    private val trainingSessionDao: TrainingSessionDao,
    private val doneExerciseDao: DoneExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao
    ) : ViewModel() {

    var trainingSessions = mutableStateListOf<TrainingSession?>()
    var currentTrainingSession by mutableStateOf<TrainingSession?>(null)
    var doneExercises = mutableStateListOf<DoneExercise?>()
    var currentDoneExercise by mutableStateOf<DoneExercise?>(null)
    var exercises = mutableStateListOf<Exercise?>()
    var currentExercise by mutableStateOf<Exercise?>(null)
    var sets = mutableStateListOf<ExerciseSet?>()


    //region TrainingSession
    fun getAllTrainingSessions(){
        viewModelScope.launch {
            trainingSessions = trainingSessionDao.getAll().toMutableStateList()
        }
    }

    fun getTrainingSessionByID(id: Int){
        viewModelScope.launch {
            currentTrainingSession = trainingSessionDao.findById(id)
        }
    }

    fun addTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.insert(trainingSession)
        }
        trainingSessions.add(trainingSession)
    }

    fun deleteTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.delete(trainingSession)
        }
        trainingSessions.remove(trainingSession)
    }

    private fun updateTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.delete(trainingSessionDao.findById(trainingSession.id))
            trainingSessionDao.insert(trainingSession)
        }
    }
    //endregion

    // region DoneExercise
    fun getAllDoneExercises(){
        viewModelScope.launch {
            doneExercises = doneExerciseDao.getAll().toMutableStateList()
        }
    }

    fun getDoneExerciseByID(id: Int){
        viewModelScope.launch {
            currentDoneExercise = doneExerciseDao.findById(id)
        }
    }

    fun insertDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.insert(doneExercise)
        }
    }

    fun deleteDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.delete(doneExercise)
        }
    }

    fun updateDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.delete(doneExercise)
            doneExerciseDao.insert(doneExercise)
        }
    }
    //endregion

    //region Exercise
    fun getAllExercises(){
        viewModelScope.launch {
            exercises = exerciseDao.getAll().toMutableStateList()
        }
    }

    fun getExerciseByID(id: Int){
        viewModelScope.launch {
            currentExercise = exerciseDao.findById(id)
        }
    }

    fun insertExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.insert(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.delete(exercise)
        }
    }

    fun updateExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.delete(exercise)
            exerciseDao.insert(exercise)
        }
    }
    //endregion

    //region ExerciseSet
    fun getSetsByIDs(ids: List<Int>){
        viewModelScope.launch {
            sets = setDao.loadAllByIds(ids.toIntArray()).toMutableStateList()
        }
    }

    fun insertSet(set: ExerciseSet){
        viewModelScope.launch {
            setDao.insert(set)
        }
    }

    fun deleteSet(set: ExerciseSet){
        viewModelScope.launch {
            setDao.delete(set)
        }
    }

    fun updateSet(set: ExerciseSet){
        viewModelScope.launch {
            setDao.delete(set)
            setDao.insert(set)
        }
    }
    //endregion

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // 1. Hole die Application aus den extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                // 2. Hole die Datenbank-Instanz
                val database = AppDatabase.getDatabase(application)

                // 3. Gib die DAOs der Datenbank an das ViewModel weiter
                return StrengthTrainingViewModel(
                    trainingSessionDao = database.trainingSessionDao(),
                    doneExerciseDao = database.doneExerciseDao(),
                    exerciseDao = database.exerciseDao(),
                    setDao = database.setDao()
                ) as T
            }
        }
    }

}





