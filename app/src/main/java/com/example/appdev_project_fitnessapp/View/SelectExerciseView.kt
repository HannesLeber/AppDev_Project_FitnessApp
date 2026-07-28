package com.example.appdev_project_fitnessapp.View

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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.DataClasses.Exercise
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel

// Display all Existing Exercises and let the user select one
// provide FloatingActionButton for Adding new Exercises
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectExerciseView(navController: NavController, strengthTrainingViewModel: StrengthTrainingViewModel) {

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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("addNewExercise") }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_new_exercise))
            }
        }
    ) { innerPadding ->
        //TODO: add content
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(strengthTrainingViewModel.exercises) { item ->
                    ExerciseItem(item!!.name , Icons.Default.FitnessCenter, item.id , onClick = {
                        strengthTrainingViewModel.selectedExercise.value = item
                        strengthTrainingViewModel.exerciseHasBeenSelected.value = true
                        navController.popBackStack()
                        })
                    }
                }
        }
    }
}






@Composable
fun ExerciseItem(title: String, icon: ImageVector, id: Int, onClick: () -> Unit = {}){
    Box(
        //TODO: make clickable and add navigation on click
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp )
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .height(30.dp)
            .fillMaxWidth()
            .clickable { onClick() },
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