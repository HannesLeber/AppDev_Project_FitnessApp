package com.example.appdev_project_fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appdev_project_fitnessapp.View.HomeScreen
import com.example.appdev_project_fitnessapp.View.StrengthTrainingView
import com.example.appdev_project_fitnessapp.View.ChooseTemplateView
import com.example.appdev_project_fitnessapp.View.EditStrengthTrainingSessionView
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import com.example.appdev_project_fitnessapp.ui.theme.AppDev_Project_FitnessAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDev_Project_FitnessAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()
                }
            }
        }
    }
}


@Composable
fun AppNavigation(strengthTrainingViewModel: StrengthTrainingViewModel = viewModel(factory = StrengthTrainingViewModel.Factory)){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        //TODO: add screens
        composable("home") { HomeScreen(navController) }
        composable("strengthTraining") { StrengthTrainingView(navController, strengthTrainingViewModel) }
        composable("chooseTemplate") { ChooseTemplateView(navController, strengthTrainingViewModel) }
        composable("editStrengthTraining") { EditStrengthTrainingSessionView(navController, strengthTrainingViewModel) }
    }
}
