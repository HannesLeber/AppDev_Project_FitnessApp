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
import com.example.appdev_project_fitnessapp.Model.TrainingTemplate
import com.example.appdev_project_fitnessapp.Model.TrainingTemplateDao
import kotlinx.coroutines.launch
import java.util.Date

class StrengthTrainingViewModel(
    private val trainingSessionDao: TrainingSessionDao,
    private val doneExerciseDao: DoneExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val trainingTemplateDao: TrainingTemplateDao
    ) : ViewModel() {

    var trainingSessions = mutableStateListOf<TrainingSession?>()
    var currentTrainingSession by mutableStateOf<TrainingSession?>(null)
    var doneExercises = mutableStateListOf<DoneExercise?>()
    var currentDoneExercise by mutableStateOf<DoneExercise?>(null)
    var exercises = mutableStateListOf<Exercise?>()
    var currentExercise by mutableStateOf<Exercise?>(null)
    var sets = mutableStateListOf<ExerciseSet?>()
    
    var trainingTemplates = mutableStateListOf<TrainingTemplate?>()

    //region TrainingSession
    fun getAllTrainingSessions(){
        viewModelScope.launch {
            trainingSessions.clear()
            trainingSessions.addAll(trainingSessionDao.getAll())
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
            getAllTrainingSessions()
        }
    }

    fun deleteTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.delete(trainingSession)
            getAllTrainingSessions()
        }
    }

    private fun updateTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            val existing = trainingSessionDao.findById(trainingSession.id)
            if (existing != null) {
                trainingSessionDao.delete(existing)
            }
            trainingSessionDao.insert(trainingSession)
            getAllTrainingSessions()
        }
    }
    //endregion

    // region DoneExercise
    fun getAllDoneExercises(){
        viewModelScope.launch {
            doneExercises.clear()
            doneExercises.addAll(doneExerciseDao.getAll())
        }
    }

    fun getDoneExerciseByID(id: Int){
        viewModelScope.launch {
            currentDoneExercise = doneExerciseDao.findById(id)
        }
    }

    suspend fun insertDoneExercise(doneExercise: DoneExercise) : Int {
        return doneExerciseDao.insert(doneExercise).toInt()
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
            exercises.clear()
            exercises.addAll(exerciseDao.getAll())
        }
    }

    fun getExerciseByID(id: Int){
        viewModelScope.launch {
            currentExercise = exerciseDao.findById(id)
        }
    }

    suspend fun insertExercise(exercise: Exercise): Int {
        return exerciseDao.insert(exercise).toInt()
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
            sets.clear()
            sets.addAll(setDao.loadAllByIds(ids.toIntArray()))
        }
    }

    suspend fun insertSet(set: ExerciseSet) : Int {
        return setDao.insert(set).toInt()
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
    
    //region Templates
    fun getAllTemplates() {
        viewModelScope.launch {
            trainingTemplates.clear()
            trainingTemplates.addAll(trainingTemplateDao.getAll())
        }
    }
    
    fun addTemplate(template: TrainingTemplate) {
        viewModelScope.launch {
            trainingTemplateDao.insert(template)
            getAllTemplates()
        }
    }
    
    fun deleteTemplate(template: TrainingTemplate) {
        viewModelScope.launch {
            trainingTemplateDao.delete(template)
            getAllTemplates()
        }
    }
    //endregion

    companion object {
        @Volatile
        private var INSTANCE: StrengthTrainingViewModel? = null

        fun getInstance(
            trainingSessionDao: TrainingSessionDao,
            doneExerciseDao: DoneExerciseDao,
            exerciseDao: ExerciseDao,
            setDao: SetDao,
            trainingTemplateDao: TrainingTemplateDao
        ): StrengthTrainingViewModel {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: StrengthTrainingViewModel(
                    trainingSessionDao,
                    doneExerciseDao,
                    exerciseDao,
                    setDao,
                    trainingTemplateDao
                ).also { INSTANCE = it }
            }
        }

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                // 1. Hole die Application aus den extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])

                // 2. Hole die Datenbank-Instanz
                val database = AppDatabase.getDatabase(application)

                // 3. Gib die DAOs der Datenbank an das ViewModel weiter
                return getInstance(
                    trainingSessionDao = database.trainingSessionDao(),
                    doneExerciseDao = database.doneExerciseDao(),
                    exerciseDao = database.exerciseDao(),
                    setDao = database.setDao(),
                    trainingTemplateDao = database.trainingTemplateDao()
                ) as T
            }
        }
    }

}
