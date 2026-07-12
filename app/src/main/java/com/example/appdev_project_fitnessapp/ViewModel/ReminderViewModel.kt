package com.example.appdev_project_fitnessapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import com.example.appdev_project_fitnessapp.Model.Repo.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderViewModel(
    private val repository: ReminderRepository
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            _reminders.value = repository.getAll()
        }
    }

    fun addReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.insert(reminder)
            repository.scheduleReminder(reminder)
            loadReminders()
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.update(reminder)

            repository.cancelReminder(reminder)

            if (reminder.enabled) {
                repository.scheduleReminder(reminder)
            }

            loadReminders()
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.delete(reminder)
            loadReminders()
        }
    }


    companion object {

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {

                val application = checkNotNull(
                    extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                )

                val database = AppDatabase.getDatabase(application)

                val repository = ReminderRepository(
                    database.reminderDao(),
                    application
                )

                return ReminderViewModel(repository) as T
            }
        }
    }
}