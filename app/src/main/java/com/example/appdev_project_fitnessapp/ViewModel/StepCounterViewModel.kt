package com.example.appdev_project_fitnessapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.Repo.StepRepository
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Date

class StepCounterViewModel(private val repository: StepRepository) : ViewModel() {

    val todaySteps: StateFlow<DailyStepData?> = repository.getTodaySteps(Date())
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val historySteps: StateFlow<List<DailyStepData>> = repository.getLastSevenDays()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                val database = AppDatabase.getDatabase(context)
                val repository = StepRepository(database.dailyStepDao())
                StepCounterViewModel(repository)
            }
        }
    }
}
