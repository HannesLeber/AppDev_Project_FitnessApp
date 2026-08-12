package com.example.appdev_project_fitnessapp.View

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appdev_project_fitnessapp.Model.DataClasses.WeightEntry
import com.example.appdev_project_fitnessapp.ViewModel.WeightViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    navController: NavController,
    viewModel: WeightViewModel
) {
    val entries by viewModel.entries.collectAsState()

    val bmiPreview = entries.firstOrNull()?.getBMI()
    val weightPreview = entries.first().weightKg
    val heightPreview = entries.first().heightCm


    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf(heightPreview.toString() ?: "") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight") },
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
                    text = "Last saved entry:",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
             Row(
                 horizontalArrangement = Arrangement.spacedBy(12.dp)
             ) {
                 Text(
                     text = "Weight: ${weightPreview.oneDecimal()} kg"
                 )
                 bmiPreview?.let {
                     Text(
                         text = "BMI: ${it.oneDecimal()} (${bmiText(bmiPreview)})"
                     )
                 }
             }
            }


            item {
                WeightChartCard(entries = entries)
            }
            item {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                WeightInput(
                    weight = weight,
                    height = height,
                    onWeightChange = { weight = it },
                    onHeightChange = { height = it },
                    onSave = {
                        val toSaveWeight = weight.toDoubleOrNull()
                        val toSaveHeight = height.toDoubleOrNull()
                        if (toSaveWeight != null && toSaveHeight != null) {
                            viewModel.addEntry(toSaveWeight, toSaveHeight)
                            weight = ""
                        }
                    }
                )
            }

            if (entries.isEmpty()) {
                item {
                    Text("No weight entries yet")
                }
            } else {
                items(entries) { entry ->
                    WeightEntryCard(
                        entry = entry,
                        onDelete = { viewModel.deleteEntry(entry) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightInput(
    weight: String,
    height: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Add new entry",
                style = MaterialTheme.typography.titleLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = weight,
                    onValueChange = onWeightChange,
                    label = { Text("Weight kg") }
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = height,
                    onValueChange = onHeightChange,
                    label = { Text("Height cm") }
                )
            }

            Button(onClick = onSave) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun WeightChartCard(entries: List<WeightEntry>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Your weight progress",
                style = MaterialTheme.typography.titleLarge
            )

            if (entries.size < 2) {
                Text("Add at least two entries to see a line chart")
            } else {
                WeightLineChart(
                    entries = entries.sortedBy { it.timestamp },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }
        }
    }
}

// KI-generiert


@Composable
private fun WeightLineChart(
    entries: List<WeightEntry>,
    modifier: Modifier = Modifier
) {
    val entries = entries.subList(max(0, entries.size - 10), entries.size)

    val lineColor = MaterialTheme.colorScheme.primary
    val guideColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {

        val weights = entries.map { it.weightKg }
        val minWeight = weights.minOrNull() ?: return@Canvas
        val maxWeight = weights.maxOrNull() ?: return@Canvas

        val range =
            (maxWeight - minWeight).takeIf { abs(it) > 0.001 } ?: 1.0

        val leftPadding = 10.dp.toPx()
        val rightPadding = 45.dp.toPx()
        val topPadding = 10.dp.toPx()
        val bottomPadding = 35.dp.toPx()

        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding

        // Guide lines + weight values on the right
        repeat(4) { index ->

            val y = topPadding + chartHeight * index / 3f
            val weight = maxWeight - range * index / 3

            drawLine(
                color = guideColor,
                start = Offset(leftPadding, y),
                end = Offset(size.width - rightPadding, y),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.drawText(
                weight.oneDecimal(),
                size.width - rightPadding + 8.dp.toPx(),
                y + 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 12.sp.toPx()
                }
            )
        }

        // Calculate points
        val points = entries.mapIndexed { index, entry ->

            val x = if (entries.size == 1) {
                leftPadding
            } else {
                leftPadding +
                        chartWidth * index / entries.lastIndex.toFloat()
            }

            val normalized =
                ((entry.weightKg - minWeight) / range).toFloat()

            val y =
                topPadding + chartHeight * (1f - normalized)

            Offset(x, y)
        }

        // Draw lines
        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Draw points
        points.forEach { point ->
            drawCircle(
                color = lineColor,
                radius = 5.dp.toPx(),
                center = point
            )
        }

        // Dates at the bottom
        entries.forEachIndexed { index, entry ->

            val point = points[index]

            val date = SimpleDateFormat(
                "dd.MM",
                Locale.getDefault()
            ).format(Date(entry.timestamp))

            drawContext.canvas.nativeCanvas.drawText(
                date,
                point.x - 15.dp.toPx(),
                size.height - 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = textColor.toArgb()
                    textSize = 12.sp.toPx()
                }
            )
        }
    }
}
// KI-generiert Ende

@Composable
private fun WeightEntryCard(
    entry: WeightEntry,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${entry.weightKg.oneDecimal()} kg",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("BMI ${entry.getBMI().oneDecimal()} (${bmiText(entry.getBMI())})")
                Text("${entry.heightCm} cm - ${entry.timestamp.toDate()}")
            }

            TextButton(onClick = onDelete) {
                Text("Remove")
            }
        }
    }
}

private fun bmiText(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}

private fun Double.oneDecimal(): String {
    return "%.1f".format(this)
}

private fun Long.toDate(): String {
    return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(this))
}
