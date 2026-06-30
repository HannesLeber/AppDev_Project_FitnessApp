package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.DoneExercise
import com.example.appdev_project_fitnessapp.Model.Exercise
import com.example.appdev_project_fitnessapp.Model.TrainingSession
import com.example.appdev_project_fitnessapp.Model.TrainingTemplate
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStrengthTrainingSessionView(navController: NavHostController, model: StrengthTrainingViewModel = viewModel(factory = StrengthTrainingViewModel.Factory)) {
    var sessionName by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<String>() }
    var newExerciseName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Neues Training") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val exerciseIds = mutableListOf<Int>()
                            selectedExercises.forEach { name ->
                                val id = model.insertExercise(Exercise(name = name, prSetID = null, doneExercises = listOf()))
                                val doneExId = model.insertDoneExercise(DoneExercise(exerciseID = id.toInt(), sets = listOf()))
                                exerciseIds.add(doneExId.toInt())
                            }
                            
                            val session = TrainingSession(
                                name = sessionName.ifBlank { "Unbenanntes Training" },
                                doneExercises = exerciseIds,
                                date = Date()
                            )
                            model.addTrainingSession(session)
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Speichern")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = sessionName,
                onValueChange = { sessionName = it },
                label = { Text("Name der Session") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Übungen hinzufügen", style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newExerciseName,
                    onValueChange = { newExerciseName = it },
                    label = { Text("Übungsname") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    if (newExerciseName.isNotBlank()) {
                        selectedExercises.add(newExerciseName)
                        newExerciseName = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(selectedExercises, key = { it + selectedExercises.indexOf(it) }) { exercise ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                selectedExercises.remove(exercise)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Color.Red
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                                    .background(color, shape = CardDefaults.shape),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Löschen",
                                        modifier = Modifier.padding(start = 16.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        enableDismissFromEndToStart = false
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = exercise,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val template = TrainingTemplate(
                            name = sessionName.ifBlank { "Template" },
                            exerciseIds = listOf()
                        )
                        model.addTemplate(template)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Als Template speichern")
            }
        }
    }
}
