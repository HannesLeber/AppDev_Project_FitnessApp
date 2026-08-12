package com.example.appdev_project_fitnessapp.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.appdev_project_fitnessapp.Model.AppDatabase
import com.example.appdev_project_fitnessapp.Model.DataClasses.WeightEntry
import com.example.appdev_project_fitnessapp.Model.Repo.WeightRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    private val _entries = MutableStateFlow<List<WeightEntry>>(emptyList())
    val entries: StateFlow<List<WeightEntry>> = _entries

    init {
        loadEntries()
    }

    fun loadEntries() {
        viewModelScope.launch {
            _entries.value = repository.getAll()
        }
    }

    fun addEntry(weightKg: Double, heightCm: Double) {
        viewModelScope.launch {
            repository.insert(
                WeightEntry(
                    weightKg = weightKg,
                    heightCm = heightCm
                )
            )
            loadEntries()
        }
    }

    fun deleteEntry(entry: WeightEntry) {
        viewModelScope.launch {
            repository.delete(entry)
            loadEntries()
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
                val repository = WeightRepository(database.weightDao())

                return WeightViewModel(repository) as T
            }
        }
    }
}
