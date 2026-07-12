package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.ReminderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel
) {

    val reminders by viewModel.reminders.collectAsState()
    var showAddDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.notifications)) },
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

            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }

    ) { padding ->

        if (reminders.isEmpty()) {

            Text(
                text = "No reminders yet",
                modifier = Modifier.padding(padding)
            )

        } else {

            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {

                items(reminders) { reminder ->

                    ReminderCard(
                        reminder = reminder,
                        onDelete = {
                            viewModel.deleteReminder(reminder)
                        },
                        onToggle = { enabled ->
                            viewModel.updateReminder(
                                reminder.copy(enabled = enabled)
                            )
                        }
                    )

                }

            }

        }

    }

    if (showAddDialog) {

        AddReminderDialog(

            onDismiss = {
                showAddDialog = false
            },

            onSave = { reminder ->

                viewModel.addReminder(reminder)

                showAddDialog = false

            }

        )

    }
}

@Composable
fun ReminderCard(
    reminder: Reminder,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {

    Card {

        Column {

            Text(reminder.title)

            Text(
                "Every ${reminder.interval} ${reminder.unit}"
            )

            Row {

                Switch(
                    checked = reminder.enabled,
                    onCheckedChange = onToggle
                )

                Button(
                    onClick = onDelete
                ) {
                    Text("Delete")
                }

            }

        }

    }

}