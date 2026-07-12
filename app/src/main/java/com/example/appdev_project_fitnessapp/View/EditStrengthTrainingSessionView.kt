package com.example.appdev_project_fitnessapp.View

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel

@Composable
fun EditStrengthTrainingSessionView(navController: NavHostController, strengthTrainingViewModel: StrengthTrainingViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                //TODO: load state from ViewModel
            } else if (event == Lifecycle.Event.ON_STOP) {
                //TODO: save state into ViewModel/DB
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    //TODO: add content





}