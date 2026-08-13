package com.example.appdev_project_fitnessapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.Repo.StepReminderSettings
import com.example.appdev_project_fitnessapp.Model.Repo.StepRepository
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    private val _goal = MutableStateFlow(repository.getGoal())
    val goal: StateFlow<Int> = _goal.asStateFlow()

    private val _reminderSettings = MutableStateFlow(repository.getReminderSettings())
    val reminderSettings: StateFlow<StepReminderSettings> = _reminderSettings.asStateFlow()

    fun updateGoal(goal: Int) {
        viewModelScope.launch {
            repository.saveGoal(goal)
            _goal.value = goal
        }
    }

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.saveReminderSettings(enabled, hour, minute)
            if (enabled) {
                repository.scheduleStepReminder(hour, minute)
            } else {
                repository.cancelStepReminder()
            }
            _reminderSettings.value = StepReminderSettings(enabled, hour, minute)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as android.app.Application)
                val database = AppDatabase.getDatabase(context)
                val repository = StepRepository(database.dailyStepDao(), context)
                StepCounterViewModel(repository)
            }
        }
    }
}
