package com.example.appdev_project_fitnessapp.View.Reminders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.example.appdev_project_fitnessapp.Helper.ExactReminderTimingManager
import com.example.appdev_project_fitnessapp.Model.DataClasses.Reminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    reminder: Reminder? = null,
    dialogTitle: String = if (reminder == null) "New Reminder" else "Edit Reminder",
    onDismiss: () -> Unit,
    onSave: (Reminder) -> Unit
) {
    var title by remember(reminder) { mutableStateOf(reminder?.title ?: "") }
    var message by remember(reminder) { mutableStateOf(reminder?.message ?: "") }
    var category by remember(reminder) { mutableStateOf(reminder?.category ?: "WATER") }
    var enabled by remember(reminder) { mutableStateOf(reminder?.enabled ?: true) }
    var interval by remember(reminder) { mutableStateOf((reminder?.interval ?: 1L).toString()) }
    var scheduleType by remember(reminder) {
        mutableStateOf(reminder?.scheduleType ?: ExactReminderTimingManager.TYPE_INTERVAL)
    }
    var hour by remember(reminder) { mutableStateOf((reminder?.hour ?: 8).toString()) }
    var minute by remember(reminder) { mutableStateOf((reminder?.minute ?: 0).toString()) }
    var startHour by remember(reminder) { mutableStateOf((reminder?.startHour ?: 8).toString()) }
    var endHour by remember(reminder) { mutableStateOf((reminder?.endHour ?: 22).toString()) }
    var weekdays by remember(reminder) { mutableStateOf(reminder?.weekdays ?: "") }
    var expanded by remember { mutableStateOf(false) }
    var unit by remember(reminder) { mutableStateOf(reminder?.unit ?: "HOURS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(dialogTitle)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") }
                )

                Text("Category")
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    listOf(
                        "WATER" to "Water",
                        "SUPPLEMENTS" to "Supplements",
                        "TRAINING" to "Training"
                    ).forEach { (value, label) ->
                        FilterChip(
                            selected = category == value,
                            onClick = { category = value },
                            label = { Text(label) }
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Notifications"
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
                if (enabled) {

                    ScheduleTypeOption(
                        selected = scheduleType == ExactReminderTimingManager.TYPE_INTERVAL,
                        onClick = { scheduleType = ExactReminderTimingManager.TYPE_INTERVAL },
                        text = "Every interval"
                    )

                    ScheduleTypeOption(
                        selected = scheduleType == ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
                        onClick = { scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME },
                        text = "Daily at exact time"
                    )

                    ScheduleTypeOption(
                        selected = scheduleType == ExactReminderTimingManager.TYPE_WINDOWED_INTERVAL,
                        onClick = {
                            scheduleType = ExactReminderTimingManager.TYPE_WINDOWED_INTERVAL
                        },
                        text = "From start to end"
                    )

                    if (scheduleType != ExactReminderTimingManager.TYPE_DAILY_AT_TIME) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = interval,
                            onValueChange = { interval = it },
                            label = { Text("Interval") }
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = unit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unit") }
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf("MINUTES", "HOURS", "DAYS").forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item) },
                                        onClick = {
                                            unit = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (scheduleType == ExactReminderTimingManager.TYPE_DAILY_AT_TIME) {
                        Row {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = hour,
                                onValueChange = { hour = it },
                                label = { Text("Hour") }
                            )

                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = minute,
                                onValueChange = { minute = it },
                                label = { Text("Minute") }
                            )
                        }

                        WeekdaySelector(
                            selectedWeekdays = weekdays,
                            onSelectedWeekdaysChange = { weekdays = it }
                        )
                    }

                    if (scheduleType == ExactReminderTimingManager.TYPE_WINDOWED_INTERVAL) {
                        Row {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = startHour,
                                onValueChange = { startHour = it },
                                label = { Text("Start hour") }
                            )

                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = endHour,
                                onValueChange = { endHour = it },
                                label = { Text("End hour") }
                            )
                        }
                    }


                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val intervalValue = interval.toLongOrNull()
                    if (scheduleType != ExactReminderTimingManager.TYPE_DAILY_AT_TIME && intervalValue == null) {
                        return@Button
                    }

                    onSave(
                        Reminder(
                            id = reminder?.id ?: 0,
                            title = title,
                            message = message,
                            category = category,
                            interval = intervalValue ?: 1L,
                            unit = unit,
                            scheduleType = scheduleType,
                            hour = hour.toIntOrNull() ?: 8,
                            minute = minute.toIntOrNull() ?: 0,
                            startHour = startHour.toIntOrNull() ?: 8,
                            endHour = endHour.toIntOrNull() ?: 22,
                            weekdays = weekdays,
                            enabled = enabled
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun WeekdaySelector(
    selectedWeekdays: String,
    onSelectedWeekdaysChange: (String) -> Unit
) {
    val selected = selectedWeekdays
        .split(",")
        .filter { it.isNotBlank() }
        .toSet()

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Weekdays")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                "MONDAY" to "Mon",
                "TUESDAY" to "Tue",
                "WEDNESDAY" to "Wed",
                "THURSDAY" to "Thu"
            ).forEach { (value, label) ->
                WeekdayChip(
                    value = value,
                    label = label,
                    selected = selected,
                    onSelectedWeekdaysChange = onSelectedWeekdaysChange
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(
                "FRIDAY" to "Fri",
                "SATURDAY" to "Sat",
                "SUNDAY" to "Sun"
            ).forEach { (value, label) ->
                WeekdayChip(
                    value = value,
                    label = label,
                    selected = selected,
                    onSelectedWeekdaysChange = onSelectedWeekdaysChange
                )
            }
        }
    }
}

@Composable
private fun WeekdayChip(
    value: String,
    label: String,
    selected: Set<String>,
    onSelectedWeekdaysChange: (String) -> Unit
) {
    FilterChip(
        selected = value in selected,
        onClick = {
            val updated = if (value in selected) {
                selected - value
            } else {
                selected + value
            }

            onSelectedWeekdaysChange(
                listOf(
                    "MONDAY",
                    "TUESDAY",
                    "WEDNESDAY",
                    "THURSDAY",
                    "FRIDAY",
                    "SATURDAY",
                    "SUNDAY"
                ).filter { it in updated }.joinToString(",")
            )
        },
        label = { Text(label) }
    )
}

@Composable
private fun ScheduleTypeOption(
    selected: Boolean,
    onClick: () -> Unit,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(text)
    }
}
