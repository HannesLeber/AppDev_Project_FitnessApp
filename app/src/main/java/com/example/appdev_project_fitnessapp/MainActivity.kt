package com.example.appdev_project_fitnessapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.notifications.NotificationHelper
import com.example.appdev_project_fitnessapp.View.HomeScreen
import com.example.appdev_project_fitnessapp.View.StrengthTrainingView
import com.example.appdev_project_fitnessapp.View.ChooseTemplateView
import com.example.appdev_project_fitnessapp.View.EditStrengthTrainingSessionView
import com.example.appdev_project_fitnessapp.View.ReminderScreen
import com.example.appdev_project_fitnessapp.ViewModel.ReminderViewModel
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import com.example.appdev_project_fitnessapp.ui.theme.AppDev_Project_FitnessAppTheme

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        enableEdgeToEdge()

        setContent {
            AppDev_Project_FitnessAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppNavigation(innerPadding)
                }
            }
        }
    }
}


@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    strengthTrainingViewModel: StrengthTrainingViewModel = viewModel(factory = StrengthTrainingViewModel.Factory),
    reminderViewModel: ReminderViewModel = viewModel(factory = ReminderViewModel.Factory)
    ){
    val navController = rememberNavController()

    NavHost(navController, startDestination = "home") {
        //TODO: add screens
        composable("home") { HomeScreen(navController) }
        composable("strengthTraining") { StrengthTrainingView(navController, strengthTrainingViewModel) }
        composable("chooseTemplate") { ChooseTemplateView(navController, strengthTrainingViewModel) }
        composable("editStrengthTraining") { EditStrengthTrainingSessionView(navController, strengthTrainingViewModel) }
        composable("reminders") { ReminderScreen(navController, reminderViewModel) }

    }
}
