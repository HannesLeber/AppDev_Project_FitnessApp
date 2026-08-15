package com.example.appdev_project_fitnessapp.View.Reminders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appdev_project_fitnessapp.Helper.ExactReminderTimingManager
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
    var dialogReminder by remember { mutableStateOf<Reminder?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val expandedCategories = remember {
        mutableStateMapOf(
            "WATER" to true,
            "SUPPLEMENTS" to false,
            "TRAINING" to false
        )
    }

    val categories = reminderCategories()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.reminders)) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
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
                    dialogReminder = null
                    showDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add reminder")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Create your own reminders or use provided templates. ",
                    style = MaterialTheme.typography.titleLarge

                )
            }
            categories.forEach { category ->
                item {
                    ReminderCategorySection(
                        category = category,
                        reminders = reminders.filter { it.category == category.category },
                        expanded = expandedCategories[category.category] == true,
                        onHeaderClick = {
                            expandedCategories[category.category] = expandedCategories[category.category] != true
                        },
                        onEdit = { reminder ->
                            dialogReminder = reminder
                            showDialog = true
                        },
                        onSave = {
                            reminder -> viewModel.addReminder(reminder)
                        },
                        onDelete = {
                            reminder -> viewModel.deleteReminder(reminder)
                        },
                        onToggleNotification = { reminder ->
                            viewModel.updateReminder(reminder.copy(enabled = !reminder.enabled))
                        }
                    )
                }
            }
        }

        if (showDialog) {
            AddReminderDialog(
                reminder = dialogReminder,
                onDismiss = { showDialog = false },
                onSave = { reminder ->
                    viewModel.addReminder(reminder)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
private fun ReminderCategorySection(
    category: ReminderCategory,
    reminders: List<Reminder>,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    onEdit: (Reminder) -> Unit,
    onSave: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit,
    onToggleNotification: (Reminder) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = category.subtitle(
                            totalCount = reminders.size,
                            enabledCount = reminders.count { it.enabled }
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Icon(
                    imageVector = if (expanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (expanded) "Hide reminders" else "Show reminders"
                )
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    category.reminders.forEach { reminder ->
                        val savedReminder = reminders.firstOrNull { it.title == reminder.title }

                        if (savedReminder == null) {
                            ReminderTemplateCard(
                                reminder = reminder,
                                onAdd = { onSave(reminder) },
                                onEdit = { onEdit(reminder) }
                            )
                        } else {
                            ReminderCard(
                                reminder = savedReminder,
                                onEdit = { onEdit(savedReminder) },
                                onDelete = { onDelete(savedReminder) },
                                onToggleNotification = { onToggleNotification(savedReminder) }
                            )
                        }
                    }

                    reminders
                        .filterNot { saved -> category.reminders.any { it.title == saved.title } }
                        .forEach { reminder ->
                            ReminderCard(
                                reminder = reminder,
                                onEdit = { onEdit(reminder) },
                                onDelete = { onDelete(reminder) },
                                onToggleNotification = { onToggleNotification(reminder) }
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun ReminderTemplateCard(
    reminder: Reminder,
    onAdd: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(reminder.message)
            Text(ExactReminderTimingManager.timingDescription(reminder))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAdd) {
                    Text("Add")
                }
                OutlinedButton(onClick = onEdit) {
                    Text("Edit")
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleNotification: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(reminder.message.ifEmpty { "No message added" })
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = reminder.enabled,
                        onCheckedChange = { onToggleNotification() }
                    )
                }
            }

            Text(ExactReminderTimingManager.timingDescription(reminder))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit) {
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Text("Remove")
                }
            }
        }
    }
}

private data class ReminderCategory(
    val category: String,
    val title: String,
    val emptyText: String,
    val reminders: List<Reminder>
) {
    fun subtitle(totalCount: Int, enabledCount: Int): String {
        return emptyText +
                if(totalCount != 0) "\n$enabledCount enabled / $totalCount total"
                else ""
    }
}

private fun reminderCategories(): List<ReminderCategory> {
    return listOf(
        ReminderCategory(
            category = "WATER",
            title = "Water",
            emptyText = "Hydration reminders",
            reminders = listOf(
                reminderTemplate(
                    title = "Drink Water",
                    message = "Time to drink a glass of water",
                    category = "WATER",
                    scheduleType = ExactReminderTimingManager.TYPE_WINDOWED_INTERVAL,
                    interval = 2,
                    unit = "HOURS",
                    startHour = 8,
                    endHour = 22
                )
            )
        ),
        ReminderCategory(
            category = "SUPPLEMENTS",
            title = "Supplements",
            emptyText = "Daily supplement reminders",
            reminders = listOf(
                reminderTemplate(
                    title = "Morning Supplements",
                    message = "Take your morning supplements",
                    category = "SUPPLEMENTS",
                    scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
                    hour = 8
                ),
                reminderTemplate(
                    title = "Noon Supplements",
                    message = "Take your noon supplements",
                    category = "SUPPLEMENTS",
                    scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
                    hour = 13
                ),
                reminderTemplate(
                    title = "Evening Supplements",
                    message = "Take your evening supplements",
                    category = "SUPPLEMENTS",
                    scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
                    hour = 20
                )
            )
        ),
        ReminderCategory(
            category = "TRAINING",
            title = "Training",
            emptyText = "Training plan reminders",
            reminders = listOf(
                reminderTemplate(
                    title = "Training Plan",
                    message = "Time for your planned training session",
                    category = "TRAINING",
                    scheduleType = ExactReminderTimingManager.TYPE_DAILY_AT_TIME,
                    hour = 18,
                    weekdays = ExactReminderTimingManager.defaultTrainingWeekdays()
                )
            )
        )
    )
}

private fun reminderTemplate(
    title: String,
    message: String,
    category: String,
    scheduleType: String,
    interval: Long = 1,
    unit: String = "HOURS",
    hour: Int = 8,
    minute: Int = 0,
    startHour: Int = 8,
    endHour: Int = 22,
    weekdays: String = ""
): Reminder {
    return Reminder(
        title = title,
        message = message,
        category = category,
        interval = interval,
        unit = unit,
        scheduleType = scheduleType,
        hour = hour,
        minute = minute,
        startHour = startHour,
        endHour = endHour,
        weekdays = weekdays,
        enabled = true
    )
}
