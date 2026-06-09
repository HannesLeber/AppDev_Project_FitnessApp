package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun strengthTrainingView(navController: NavHostController){
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
        Text(stringResource(id = R.string.strength_training))

    }

}