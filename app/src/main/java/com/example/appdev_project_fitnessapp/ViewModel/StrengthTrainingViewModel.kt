package com.example.appdev_project_fitnessapp.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.DataClasses.DoneExercise
import com.example.appdev_project_fitnessapp.Model.DAOs.DoneExerciseDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.Exercise
import com.example.appdev_project_fitnessapp.Model.DAOs.ExerciseDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.ExerciseSet
import com.example.appdev_project_fitnessapp.Model.DAOs.SetDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingSession
import com.example.appdev_project_fitnessapp.Model.DAOs.TrainingSessionDao
import kotlinx.coroutines.launch

class StrengthTrainingViewModel(
    private val trainingSessionDao: TrainingSessionDao,
    private val doneExerciseDao: DoneExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao
    ) : ViewModel() {

    // Using 'val' for SnapshotStateLists. 
    // Changes to the content (clear, add) will automatically trigger UI updates
    // as long as the reference to the list object remains the same.

    val trainingSessions = mutableStateListOf<TrainingSession?>() //val, weil mit den .clear() und .add() funktionen dann die UI aktualisiert wird.
    var currentTrainingSession by mutableStateOf<TrainingSession?>(null)
    val doneExercises = mutableStateListOf<DoneExercise?>()
    var currentDoneExercise by mutableStateOf<DoneExercise?>(null)
    val exercises = mutableStateListOf<Exercise?>()
    var currentExercise by mutableStateOf<Exercise?>(null)
    val sets = mutableStateListOf<ExerciseSet?>()

    //region TrainingSession
    fun getAllTrainingSessions(){
        viewModelScope.launch {
            val sessions = trainingSessionDao.getAll()
            trainingSessions.clear()
            trainingSessions.addAll(sessions)
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
            trainingSessionDao.delete(trainingSessionDao.findById(trainingSession.id))
            trainingSessionDao.insert(trainingSession)
            getAllTrainingSessions()
        }
    }
    //endregion

    // region DoneExercise
    fun getAllDoneExercises(){
        viewModelScope.launch {
            val items = doneExerciseDao.getAll()
            doneExercises.clear()
            doneExercises.addAll(items)
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
            getAllDoneExercises()
        }
    }

    fun deleteDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.delete(doneExercise)
            getAllDoneExercises()
        }
    }

    fun updateDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.delete(doneExercise)
            doneExerciseDao.insert(doneExercise)
            getAllDoneExercises()
        }
    }
    //endregion

    //region Exercise
    fun getAllExercises(){
        viewModelScope.launch {
            val items = exerciseDao.getAll()
            exercises.clear()
            exercises.addAll(items)
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
            getAllExercises()
        }
    }

    fun deleteExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.delete(exercise)
            getAllExercises()
        }
    }

    fun updateExercise(exercise: Exercise){
        viewModelScope.launch {
            exerciseDao.delete(exercise)
            exerciseDao.insert(exercise)
            getAllExercises()
        }
    }
    //endregion

    //region ExerciseSet
    fun getSetsByIDs(ids: List<Int>){
        viewModelScope.launch {
            val items = setDao.loadAllByIds(ids.toIntArray())
            sets.clear()
            sets.addAll(items)
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


    fun loadData(){
        getAllTrainingSessions()
        getAllDoneExercises()
        getAllExercises()
    }

    companion object {
        @Volatile
        private var instance: StrengthTrainingViewModel? = null

        fun getInstance(
            trainingSessionDao: TrainingSessionDao,
            doneExerciseDao: DoneExerciseDao,
            exerciseDao: ExerciseDao,
            setDao: SetDao
        ): StrengthTrainingViewModel {
            return instance ?: synchronized(this) {
                instance ?: StrengthTrainingViewModel(
                    trainingSessionDao,
                    doneExerciseDao,
                    exerciseDao,
                    setDao
                ).also { instance = it }
            }
        }

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = AppDatabase.getDatabase(application)

                return getInstance(
                    database.trainingSessionDao(),
                    database.doneExerciseDao(),
                    database.exerciseDao(),
                    database.setDao()
                ) as T
            }
        }
    }
}
