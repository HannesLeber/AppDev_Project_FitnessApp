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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthTrainingView(navController: NavHostController, model: StrengthTrainingViewModel = viewModel(factory = StrengthTrainingViewModel.Factory)){

    //TODO: add variables (trainingsessions)
    var trainingSessions = remember { mutableStateListOf<TrainingSession?>(null) }

    LaunchedEffect(Unit) {
        //TODO: get Variables from DB
        model.getAllTrainingSessions()
        trainingSessions = model.trainingSessions

        if (trainingSessions.isEmpty()){
            model.addTrainingSession(TrainingSession(name = "Test", doneExercises = listOf(), date = Date()))
            model.addTrainingSession(TrainingSession(name = "Test2", doneExercises = listOf(), date = Date()))
            model.addTrainingSession(TrainingSession(name = "Test3", doneExercises = listOf(), date = Date()))
        }
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
                items(model.trainingSessions){ item ->
                    TrainingSessionItem(item?.name ?: "none", Icons.Default.FitnessCenter)

                }

                //TODO: add other items
            }
        }


    }

}


@Composable
fun TrainingSessionItem(title: String, icon: ImageVector){
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
        }
    }

}