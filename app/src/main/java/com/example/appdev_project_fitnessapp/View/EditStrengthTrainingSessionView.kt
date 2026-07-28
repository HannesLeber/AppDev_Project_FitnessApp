package com.example.appdev_project_fitnessapp.View

import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.DataClasses.DoneExercise
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingSession
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingTemplate
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStrengthTrainingSessionView(navController: NavHostController, strengthTrainingViewModel: StrengthTrainingViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var sessionName by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<DoneExercise>() }
    var selectedExercisesIDs by remember { mutableStateOf(listOf<Int>()) }
    val scope = rememberCoroutineScope()
    val unknowntrainingString = stringResource(R.string.unknown_training)
    val session = strengthTrainingViewModel.currentTrainingSession

    var isNewTrainingSession by remember { mutableStateOf(false) }


    LaunchedEffect(strengthTrainingViewModel.exerciseHasBeenSelected.value) {
        if (strengthTrainingViewModel.exerciseHasBeenSelected.value) {
            val selected = strengthTrainingViewModel.selectedExercise.value
            if (selected != null) {
                val doneExercise = DoneExercise(exerciseID = selected.id, sets = listOf())
                val doneExerciseId = strengthTrainingViewModel.insertDoneExercise(doneExercise)
                val persistedDoneExercise = strengthTrainingViewModel.getDoneExerciseByID(doneExerciseId)
                if (persistedDoneExercise != null) {
                    selectedExercises.add(persistedDoneExercise)
                }
            }
            strengthTrainingViewModel.exerciseHasBeenSelected.value = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                Log.d("Lifecycle", "ON_START")
                if (strengthTrainingViewModel.currentTrainingSession != null) {
                    sessionName = session?.name ?:"None"
                    selectedExercisesIDs = session!!.doneExercises
                    
                    selectedExercises.clear()
                    scope.launch {
                        val exercises = strengthTrainingViewModel.getDoneExercisesByIDs(selectedExercisesIDs)
                        selectedExercises.addAll(exercises)
                    }
                    
                    Log.d("Lifecycle", "is not new Trainingsession")
                    isNewTrainingSession = false
                } else{
                    isNewTrainingSession = true
                    Log.d("Lifecycle", "is new Trainingsession")
                }

            }
            else if (event == Lifecycle.Event.ON_STOP) {
                Log.d("Lifecycle", "ON_STOP")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.edit_strength_training)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val exerciseIds = mutableListOf<Int>()
                            selectedExercises.forEach { exercise ->
                                val doneExId = strengthTrainingViewModel.insertDoneExercise(
                                    DoneExercise(exerciseID = exercise.exerciseID, sets = listOf())
                                )
                                exerciseIds.add(doneExId)
                            }


                            if (isNewTrainingSession) {
                                val tempSession = TrainingSession(
                                    name = sessionName.ifBlank { unknowntrainingString },
                                    doneExercises = exerciseIds,
                                    date = Date()
                                )
                                strengthTrainingViewModel.addTrainingSession(tempSession)
                            } else{
                                Log.d("EditStrengthTrainingSessionView", "{${session?.id}}")
                                session!!.name = sessionName.ifBlank { unknowntrainingString }
                                session.doneExercises = exerciseIds
                                strengthTrainingViewModel.updateTrainingSession(session)
                            }
                            navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(id = R.string.save))
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
                label = { Text(stringResource(R.string.name_of_session)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(id = R.string.exercises), style = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.add_new_exercise))
                IconButton(onClick = {
                    navController.navigate("selectExercise")

                }) {

                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(selectedExercises, key = { it.id }) { exercise ->
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
                                        contentDescription = stringResource(id = R.string.delete),
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
                                text = strengthTrainingViewModel.exercises.find { it?.id == exercise.exerciseID }?.name ?: stringResource(id = R.string.unknown_exercise),
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
                            exerciseIds = selectedExercises.map { it.exerciseID }
                        )
                        strengthTrainingViewModel.addTemplate(template)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_as_template))
            }
        }
    }
}
