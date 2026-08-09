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

    val trainingSessions = mutableStateListOf<TrainingSession?>() 
    var trainingSessionToBeEdited by mutableStateOf<TrainingSession?>(null)
    val doneExercises = mutableStateListOf<DoneExercise?>()
    var DoneExerciseToBeEdited by mutableStateOf<DoneExercise?>(null)
    val exercises = mutableStateListOf<Exercise?>()
    var ExerciseToBeEdited by mutableStateOf<Exercise?>(null)
    val sets = mutableStateListOf<ExerciseSet>()
    val currentPrSet = mutableStateOf<ExerciseSet?>(null)
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
            doneExerciseDao.update(doneExercise.id, doneExercise.exerciseID, doneExercise.sets)
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

    fun getPrSetByExerciseID(exerciseID: Int){
        viewModelScope.launch {
            refreshPrSet(exerciseID)
        }
    }

    suspend fun insertSet(set: ExerciseSet): Int {
        val id = setDao.insert(set).toInt()
        return id
    }

    fun deleteSet(set: ExerciseSet, doneExercise: DoneExercise){
        viewModelScope.launch {
            setDao.delete(set)
            val updatedSets = doneExercise.sets.filter { it != set.id }
            doneExerciseDao.update(doneExercise.id, doneExercise.exerciseID, updatedSets)
            updatePrSetForExercise(doneExercise.exerciseID)
        }
    }

    fun updateSet(set: ExerciseSet, exerciseID: Int){
        val setIndex = sets.indexOfFirst { it.id == set.id }
        if (setIndex != -1) {
            sets[setIndex] = set
        }
        if (currentPrSet.value?.id == set.id) {
            currentPrSet.value = set
        }
        viewModelScope.launch {
            setDao.update(set.id, set.reps, set.weight, set.warmupSet)
            updatePrSetForExercise(exerciseID, set)
        }
    }

    private fun weightToDouble(weight: String): Double? {
        val trimmedWeight = weight.trim()
        if (!Regex("\\d+(?:[,.]\\d+)?").matches(trimmedWeight)) {
            return null
        }
        return trimmedWeight.replace(',', '.').toDoubleOrNull()
    }

    private suspend fun updatePrSetForExercise(exerciseID: Int, savedSet: ExerciseSet? = null) {
        val doneExercises = doneExerciseDao.findByExerciseID(exerciseID).sortedBy { it.id }
        val setIDs = doneExercises.flatMap { it.sets }.filter { it != 0 }
        val loadedSets = if (setIDs.isEmpty()) {
            listOf()
        } else {
            setDao.loadAllByIds(setIDs.toIntArray()).sortedBy { it.id }
        }
        val setsToCompare = if (savedSet == null) {
            loadedSets
        } else if (loadedSets.any { it.id == savedSet.id }) {
            loadedSets.map { if (it.id == savedSet.id) savedSet else it }
        } else {
            loadedSets + savedSet
        }
        if (setsToCompare.isEmpty()) {
            exerciseDao.updatePrSetID(exerciseID, null)
            currentPrSet.value = null
            getAllExercises()
            return
        }
        var prSetID: Int? = null
        var prWeight: Double? = null

        setsToCompare.forEach {
            val weight = weightToDouble(it.weight)
            if (weight != null && (prWeight == null || weight > prWeight!!)) {
                prSetID = it.id
                prWeight = weight
            }
        }

        exerciseDao.updatePrSetID(exerciseID, prSetID)
        refreshPrSet(exerciseID)
        getAllExercises()
    }

    private suspend fun refreshPrSet(exerciseID: Int) {
        val exercise = exerciseDao.findById(exerciseID)
        currentPrSet.value = exercise.prSetID?.let { setDao.loadAllByIds(intArrayOf(it)).firstOrNull() }
    }
    //endregion

    //region Template
    fun getAllTemplates(){
        viewModelScope.launch {
            refreshTemplatesList()
        }
    }

    // Interne Hilfsfunktion für konsistente Updates
    private suspend fun refreshTemplatesList() {
        val temp = trainingTemplateDao.getAll()
        templates.clear()
        templates.addAll(temp)
    }

    fun addTemplate(template: TrainingTemplate) {
        viewModelScope.launch {
            trainingTemplateDao.insert(template)
            refreshTemplatesList()
        }
    }

    fun deleteTemplate(template: TrainingTemplate){
        viewModelScope.launch {
            trainingTemplateDao.delete(template)
            refreshTemplatesList()
        }
    }

    fun updateTemplate(template: TrainingTemplate){
        viewModelScope.launch {
            trainingTemplateDao.delete(template)
            trainingTemplateDao.insert(template)
            refreshTemplatesList()
        }
    }
    //endregion

    fun loadData(){
        getAllTrainingSessions()
        getAllDoneExercises()
        getAllExercises()
        getAllTemplates()
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
