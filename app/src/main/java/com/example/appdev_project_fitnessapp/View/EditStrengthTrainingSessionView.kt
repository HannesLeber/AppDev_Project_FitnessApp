package com.example.appdev_project_fitnessapp.View

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
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

    var sessionToBeEdited_Name by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<DoneExercise>() }
    var selectedExercisesIDs by remember { mutableStateOf(listOf<Int>()) }
    val scope = rememberCoroutineScope()
    val unknowntrainingString = stringResource(R.string.unknown_training) //just a String, that i can't get later in the coroutine
    val sessionToBeEdited = strengthTrainingViewModel.trainingSessionToBeEdited

    var isNewTrainingSession by remember { mutableStateOf(false) }

    /**
     * if the last screen was the SelectExerciseView, and the user has selected an exercise,
     * load it into the selectedExercises list. (temporarily, until the user saves the session)
     */
    LaunchedEffect(strengthTrainingViewModel.exerciseHasBeenSelected.value) {
        if (strengthTrainingViewModel.exerciseHasBeenSelected.value) {
            val selected = strengthTrainingViewModel.selectedExercise.value
            if (selected != null) {
                val doneExercise = DoneExercise(exerciseID = selected.id, sets = listOf())
                val doneExerciseId = strengthTrainingViewModel.insertDoneExercise(doneExercise)
                val persistedDoneExercise = strengthTrainingViewModel.getDoneExerciseByID(doneExerciseId)
                if (persistedDoneExercise != null) {
                    selectedExercises.add(persistedDoneExercise)
                    strengthTrainingViewModel.temprarySelectedDoneExercises.add(persistedDoneExercise)
                }
            }
            strengthTrainingViewModel.exerciseHasBeenSelected.value = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                /**
                 * Load Exercises within this Session if it is an existing Session
                 * else set isNewTrainingSession to true
                 */
                if (strengthTrainingViewModel.trainingSessionToBeEdited != null) {
                    sessionToBeEdited_Name = sessionToBeEdited?.name ?:"None"
                    selectedExercisesIDs = sessionToBeEdited!!.doneExercises
                    
                    selectedExercises.clear()
                    scope.launch {
                        val exercises = strengthTrainingViewModel.getDoneExercisesByIDs(selectedExercisesIDs)
                        selectedExercises.addAll(exercises)
                        if(strengthTrainingViewModel.temprarySelectedDoneExercises.isNotEmpty()){
                            selectedExercises.addAll(strengthTrainingViewModel.temprarySelectedDoneExercises.toList() as Collection<DoneExercise>)
                        }
                        if(strengthTrainingViewModel.temporaryDeletedDoneExercises.isNotEmpty()){
                            Log.d("EditStrengthTrainingSessionView", "deleting currentDeletedDoneExercises")
                            strengthTrainingViewModel.temporaryDeletedDoneExercises.forEach { doneExercise ->
                                if(doneExercise != null){
                                    val removed = selectedExercises.remove(doneExercise)
                                    Log.d("EditStrengthTrainingSessionView", "deleting $doneExercise ${if(removed) "successful" else "failed"}")
                                }
                            }
                        }
                    }
                    Log.d("EditStrengthTrainingSessionView", "is not new Trainingsession")
                    isNewTrainingSession = false //should be unnecessary, but better save than sorry
                } else{
                    isNewTrainingSession = true
                    Log.d("EditStrengthTrainingSessionView", "is new Trainingsession")
                }
                if(strengthTrainingViewModel.temporarySessionName.isNotBlank()){
                    sessionToBeEdited_Name = strengthTrainingViewModel.temporarySessionName
                }
            }
            else if (event == Lifecycle.Event.ON_STOP) {
                //save Values
                strengthTrainingViewModel.temporarySessionName = sessionToBeEdited_Name
                Log.d("EditStrengthTrainingSessionView", "ON_STOP")
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
                                if (strengthTrainingViewModel.getDoneExerciseByID(exercise.id) == null) {
                                    val doneExId = strengthTrainingViewModel.insertDoneExercise(
                                        DoneExercise(exerciseID = exercise.exerciseID, sets = listOf())
                                    )
                                    exerciseIds.add(doneExId)
                                } else {
                                    val doneExId = exercise.id
                                    exerciseIds.add(doneExId)
                                }

                            }


                            if (isNewTrainingSession) {
                                val tempSession = TrainingSession(
                                    name = sessionToBeEdited_Name.ifBlank { unknowntrainingString },
                                    doneExercises = exerciseIds,
                                    date = Date()
                                )
                                strengthTrainingViewModel.addTrainingSession(tempSession)
                            } else{
                                Log.d("EditStrengthTrainingSessionView", "{${sessionToBeEdited?.id}}")
                                sessionToBeEdited!!.name = sessionToBeEdited_Name.ifBlank { unknowntrainingString }
                                sessionToBeEdited.doneExercises = exerciseIds
                                strengthTrainingViewModel.updateTrainingSession(sessionToBeEdited)
                            }
                            if(strengthTrainingViewModel.temporaryDeletedDoneExercises.isNotEmpty()){
                                strengthTrainingViewModel.deleteDoneExercises(strengthTrainingViewModel.temporaryDeletedDoneExercises.toList() as List<DoneExercise>)
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
                value = sessionToBeEdited_Name,
                onValueChange = { sessionToBeEdited_Name = it },
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
                items(selectedExercises, key = { it.id }) { doneExercise ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                selectedExercises.remove(doneExercise)
                                strengthTrainingViewModel.temprarySelectedDoneExercises.remove(doneExercise)
                                strengthTrainingViewModel.temporaryDeletedDoneExercises.add(doneExercise)
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
                        DoneExerciseItem(strengthTrainingViewModel.exercises.find { it?.id == doneExercise.exerciseID }?.name ?: stringResource(id = R.string.unknown_exercise), Icons.Default.Add, onClick = {
                            strengthTrainingViewModel.DoneExerciseToBeEdited = doneExercise
                            navController.navigate("editDoneExercise")
                        })
                    }
                }
            }

            Button(
                onClick = {
                    scope.launch {
                        val template = TrainingTemplate(
                            name = sessionToBeEdited_Name.ifBlank { "Template" },
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


@Composable
fun DoneExerciseItem(title: String, icon: ImageVector, onClick: () -> Unit = {}){
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp )
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .height(30.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ){
        Row(){
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(5.dp))
            Text(title)
            Spacer(modifier = Modifier.width(5.dp))
        }
    }

}
