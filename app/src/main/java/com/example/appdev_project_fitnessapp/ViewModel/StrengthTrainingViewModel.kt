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
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingTemplate
import com.example.appdev_project_fitnessapp.Model.DAOs.TrainingTemplateDao
import kotlinx.coroutines.launch

class StrengthTrainingViewModel(
    private val trainingSessionDao: TrainingSessionDao,
    private val doneExerciseDao: DoneExerciseDao,
    private val exerciseDao: ExerciseDao,
    private val setDao: SetDao,
    private val trainingTemplateDao: TrainingTemplateDao
    ) : ViewModel() {

    // Using 'val' for SnapshotStateLists. 
    // Changes to the content (clear, add) will automatically trigger UI updates
    // as long as the reference to the list object remains the same.

    val trainingSessions = mutableStateListOf<TrainingSession?>() //val, weil mit den .clear() und .add() funktionen dann die UI aktualisiert wird.
    var trainingSessionToBeEdited by mutableStateOf<TrainingSession?>(null)
    val doneExercises = mutableStateListOf<DoneExercise?>()
    var DoneExerciseToBeEdited by mutableStateOf<DoneExercise?>(null)
    val exercises = mutableStateListOf<Exercise?>()
    var ExerciseToBeEdited by mutableStateOf<Exercise?>(null)
    val sets = mutableStateListOf<ExerciseSet?>()
    val temprarySelectedDoneExercises = mutableStateListOf<DoneExercise?>()
    val temporaryDeletedDoneExercises = mutableStateListOf<DoneExercise?>()

    var selectedExercise = mutableStateOf<Exercise?>(null)
    var exerciseHasBeenSelected = mutableStateOf(false)

    val templates = mutableStateListOf<TrainingTemplate?>()

    var temporarySessionName by mutableStateOf("")



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
            trainingSessionToBeEdited = trainingSessionDao.findById(id)

        }
    }

    suspend fun addTrainingSession(trainingSession: TrainingSession): Int {
        val id = trainingSessionDao.insert(trainingSession).toInt()
        getAllTrainingSessions()
        return id
    }

    fun deleteTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.delete(trainingSession)
            getAllTrainingSessions()
        }
    }

    fun updateTrainingSession(trainingSession: TrainingSession){
        viewModelScope.launch {
            trainingSessionDao.update(trainingSession.id, trainingSession.name!!, trainingSession.doneExercises)
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

    suspend fun getDoneExerciseByID(id: Int) : DoneExercise?{
        DoneExerciseToBeEdited = doneExerciseDao.findById(id)
        return DoneExerciseToBeEdited
    }

    suspend fun getDoneExercisesByIDs(ids: List<Int>): List<DoneExercise> {
        return doneExerciseDao.loadAllByIds(ids.toIntArray()).sortedBy { it.id }
    }

    suspend fun insertDoneExercise(doneExercise: DoneExercise): Int {
        val id = doneExerciseDao.insert(doneExercise).toInt()
        getAllDoneExercises()
        return id
    }


    fun deleteDoneExercise(doneExercise: DoneExercise){
        viewModelScope.launch {
            doneExerciseDao.delete(doneExercise)
            getAllDoneExercises()
        }
    }

    fun deleteDoneExercises(doneExercises: List<DoneExercise?>){
        doneExercises.forEach({
            if (it != null){
                deleteDoneExercise(it)
            }
        })
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
            ExerciseToBeEdited = exerciseDao.findById(id)
        }
    }

    suspend fun insertExercise(exercise: Exercise, useAsSelectedExercise: Boolean = false): Int {
        val id = exerciseDao.insert(exercise).toInt()
        getAllExercises()
        if (useAsSelectedExercise) {
            selectedExercise.value = exercise.copy(id = id)
            exerciseHasBeenSelected.value = true
        }
        return id
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

    suspend fun insertSet(set: ExerciseSet): Int {
        val id = setDao.insert(set).toInt()
        return id
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

//region Template
    suspend fun addTemplate(template: TrainingTemplate): Int {
        val id = trainingTemplateDao.insert(template).toInt()
        getAllTemplates()
        return id
    }

    fun deleteTemplate(template: TrainingTemplate){
        viewModelScope.launch {
            trainingTemplateDao.delete(template)
        }
    }

    fun updateTemplate(template: TrainingTemplate){
        viewModelScope.launch {
            trainingTemplateDao.delete(template)
            trainingTemplateDao.insert(template)
        }
    }

    fun getAllTemplates(){
        viewModelScope.launch {
            val temp = trainingTemplateDao.getAll()
            templates.clear()
            templates.addAll(temp)
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
            setDao: SetDao,
            trainingTemplateDao: TrainingTemplateDao
        ): StrengthTrainingViewModel {
            return instance ?: synchronized(this) {
                instance ?: StrengthTrainingViewModel(
                    trainingSessionDao,
                    doneExerciseDao,
                    exerciseDao,
                    setDao,
                    trainingTemplateDao
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
                    database.setDao(),
                    database.trainingTemplateDao()
                ) as T
            }
        }
    }
}
