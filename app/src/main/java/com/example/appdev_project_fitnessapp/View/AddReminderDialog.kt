package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit

) {

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    var unit: String by remember {
        mutableStateOf("HOURS")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Reminder")
        },

        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") }
                )

                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Interval") }
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }

                ) {

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Unit")
                        }

                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }

                    ) {
                        listOf(
                            "MINUTES",
                            "HOURS",
                            "DAYS"
                        ).forEach {

                            DropdownMenuItem(
                                text = {
                                    Text(it)
                                },
                                onClick = {
                                    unit = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    if (title.isBlank() || interval.isBlank()) return@Button

                    onSave(
                        Reminder(
                            title = title,
                            message = message,
                            interval = interval.toLong(),
                            unit = unit,
                            enabled = true
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}