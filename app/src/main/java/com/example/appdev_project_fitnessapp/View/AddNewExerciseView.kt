package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdev_project_fitnessapp.Model.DataClasses.Exercise
import com.example.appdev_project_fitnessapp.Model.DataClasses.TrainingTemplate
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewExerciseView(navController: NavController, strengthTrainingViewModel: StrengthTrainingViewModel) {
    var exerciseName by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_exercise)) },
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
                },
                actions = {
                    IconButton(onClick = {
                        val exercise = Exercise(name = exerciseName, prSetID = null, doneExercises = listOf())
                        strengthTrainingViewModel.insertExercise(exercise, true)
                        navController.navigate("editStrengthTraining") {
                            popUpTo("editStrengthTraining") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Save, contentDescription = stringResource(id = R.string.save))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                label = { Text(stringResource(id = R.string.name_of_exercise)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(30.dp))

            Button(onClick = {
                val exercise = Exercise(name = exerciseName, prSetID = null, doneExercises = listOf())
                strengthTrainingViewModel.insertExercise(exercise, true)
                navController.navigate("editStrengthTraining") {
                    popUpTo("editStrengthTraining") { inclusive = true }
                }
            },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.create_exercise))
            }


        }
    }

}
