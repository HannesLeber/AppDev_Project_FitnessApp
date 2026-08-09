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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
fun ChooseTemplateView(navController: NavHostController, strengthTrainingViewModel: StrengthTrainingViewModel) {
    val scope = rememberCoroutineScope()
    val templates = strengthTrainingViewModel.templates
    var templateToDelete by remember { mutableStateOf<TrainingTemplate?>(null) }

    LaunchedEffect(Unit) {
        strengthTrainingViewModel.getAllTemplates()

    }

    if (templateToDelete != null) {
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = { Text(stringResource(R.string.delete_template)) },
            text = { Text("Möchtest du das Template '${templateToDelete?.name}' wirklich löschen?") },
            confirmButton = {
                TextButton(onClick = {
                    templateToDelete?.let {
                        Log.d("ChooseTemplateView", "deleting $it")
                        strengthTrainingViewModel.deleteTemplate(it)
                        Log.d("ChooseTemplateView", "deleted $it")

                    }

                    templateToDelete = null
                }) {
                    Text(stringResource(R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Log.d("ChooseTemplateView", "cancel delete of $templateToDelete")
                    templateToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.choose_template)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (templates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_templates_available))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(templates, key = {it?.id ?: 0}) { item ->
                        if (item != null) {
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.StartToEnd) {
                                        templateToDelete = item
                                        false
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
                                                contentDescription = stringResource(R.string.delete),
                                                modifier = Modifier.padding(start = 16.dp),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                },
                                enableDismissFromEndToStart = false
                            ) {
                                TemplateItem(
                                    title = item.name,
                                    icon = Icons.Default.FitnessCenter,
                                    onClick = {
                                        scope.launch {
                                            val doneExerciseIds = mutableListOf<Int>()
                                            item.exerciseIds.forEach { exerciseId ->
                                                val deId = strengthTrainingViewModel.insertDoneExercise(
                                                    DoneExercise(exerciseID = exerciseId, sets = listOf())
                                                )
                                                doneExerciseIds.add(deId)
                                            }

                                            val newSession = TrainingSession(
                                                name = item.name,
                                                doneExercises = doneExerciseIds,
                                                date = Date()
                                            )

                                            val sessionId = strengthTrainingViewModel.addTrainingSession(newSession)

                                            // Set the session to be edited in the shared ViewModel
                                            strengthTrainingViewModel.trainingSessionToBeEdited = newSession.copy(id = sessionId)

                                            // Clear temporary states used in EditStrengthTrainingSessionView
                                            strengthTrainingViewModel.temporarySessionName = ""
                                            strengthTrainingViewModel.temprarySelectedDoneExercises.clear()
                                            strengthTrainingViewModel.temporaryDeletedDoneExercises.clear()
                                            strengthTrainingViewModel.exerciseHasBeenSelected.value = false

                                            navController.navigate("editStrengthTraining") {
                                                popUpTo("chooseTemplate") { inclusive = true }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .height(30.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}
