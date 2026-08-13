package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdev_project_fitnessapp.Model.Repo.StepReminderSettings

@Composable
fun StepGoalAndReminderDialog(
    currentGoal: Int,
    currentReminderSettings: StepReminderSettings,
    onDismiss: () -> Unit,
    onSave: (goal: Int, enabled: Boolean, hour: Int, minute: Int) -> Unit
) {
    var goal by remember { mutableStateOf(currentGoal.toString()) }
    var reminderEnabled by remember { mutableStateOf(currentReminderSettings.enabled) }
    var hour by remember { mutableStateOf(currentReminderSettings.hour.toString()) }
    var minute by remember { mutableStateOf(currentReminderSettings.minute.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ziel & Erinnerung") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Tagesziel (Schritte)") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Erinnerung aktivieren"
                    )
                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                }

                if (reminderEnabled) {
                    Row {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = hour,
                            onValueChange = { hour = it },
                            label = { Text("Stunde") }
                        )

                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = minute,
                            onValueChange = { minute = it },
                            label = { Text("Minute") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val goalValue = goal.toIntOrNull()?.coerceAtLeast(1) ?: currentGoal
                    val hourValue = hour.toIntOrNull()?.coerceIn(0, 23) ?: 18
                    val minuteValue = minute.toIntOrNull()?.coerceIn(0, 59) ?: 0

                    onSave(goalValue, reminderEnabled, hourValue, minuteValue)
                }
            ) {
                Text("Speichern")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}
