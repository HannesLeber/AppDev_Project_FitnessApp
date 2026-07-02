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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.TrainingSession
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Thread.sleep
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthTrainingView(navController: NavHostController, strengthTrainingViewModel: StrengthTrainingViewModel ){

    //TODO: add variables (trainingsessions)
    var showDialog by remember { mutableStateOf(false) }
    val trainingSessions = remember {strengthTrainingViewModel.trainingSessions}
    var sessionToDelete by remember { mutableStateOf<TrainingSession?>(null) }

    LaunchedEffect(Unit) {
        //TODO: get Variables from DB
        strengthTrainingViewModel.loadData()
        trainingSessions.clear()
        trainingSessions.addAll(strengthTrainingViewModel.trainingSessions)
//        if (strengthTrainingViewModel.trainingSessions.isEmpty()){
//            strengthTrainingViewModel.addTrainingSession(TrainingSession(name = "Test", doneExercises = listOf(), date = Date()))
//            strengthTrainingViewModel.addTrainingSession(TrainingSession(name = "Test2", doneExercises = listOf(), date = Date()))
//            strengthTrainingViewModel.addTrainingSession(TrainingSession(name = "Test3", doneExercises = listOf(), date = Date()))
//        }

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Neues Training") },
            text = { Text("Möchtest du ein leeres Training starten oder ein Template auswählen?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("editStrengthTraining")
                }) {
                    Text("Neu")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.navigate("chooseTemplate")
                }) {
                    Text("Template")
                }
            }
        )
    }
    //Dialog if User really wants to delete a Session.
    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Training löschen") },
            text = { Text("Möchtest du das Training '${sessionToDelete?.name}' wirklich löschen?") },
            confirmButton = {
                TextButton(onClick = {
                    sessionToDelete?.let { strengthTrainingViewModel.deleteTrainingSession(it) }
                    sessionToDelete = null
                }) {
                    Text("Löschen", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Abbrechen")
                }
            }
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.strength_training)) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        //Back-Button (go one page back (page = Entry in the backstack))
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Training")
            }
        }
    ) { innerPadding ->
        //TODO: add content
        Column(modifier = Modifier
            .padding(innerPadding)
        ){
            Text(stringResource(id = R.string.strength_training))
            LazyColumn(modifier = Modifier
                .fillMaxSize()
            ) {
                items(trainingSessions){ item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                sessionToDelete = item
                                false //returning false because want to ask again, if user really wants to delete the item, before deleting it. (Alertdialog)
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Color.Red else Color.Transparent
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(color, shape = RoundedCornerShape(16.dp)),
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
                        TrainingSessionItem(
                            item?.name ?: "none",
                            Icons.Default.FitnessCenter,
                            item?.id ?: 0
                        )
                    }
                }

                //TODO: add other items
            }
        }


    }

}


@Composable
fun TrainingSessionItem(title: String, icon: ImageVector, id: Int){
    Box(
        //TODO: make clickable and add navigation on click
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp )
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .height(30.dp)
            .fillMaxWidth(),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ){
        Row(){
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(5.dp))
            Text(title)
            Spacer(modifier = Modifier.width(5.dp))
            Text(id.toString())
        }
    }

}