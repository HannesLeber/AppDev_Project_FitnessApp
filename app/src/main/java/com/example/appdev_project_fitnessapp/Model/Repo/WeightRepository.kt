package com.example.appdev_project_fitnessapp.Model.Repo

import com.example.appdev_project_fitnessapp.Model.DAOs.WeightDao
import com.example.appdev_project_fitnessapp.Model.DataClasses.WeightEntry

class WeightRepository(
    private val dao: WeightDao
) {
    suspend fun insert(entry: WeightEntry) {
        dao.insert(entry)
    }

    suspend fun delete(entry: WeightEntry) {
        dao.delete(entry)
    }

    suspend fun getAll(): List<WeightEntry> {
        return dao.getAll()
    }
}
