package com.example.appdev_project_fitnessapp.View

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appdev_project_fitnessapp.Model.DataClasses.DailyStepData
import com.example.appdev_project_fitnessapp.ViewModel.StepCounterViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepCounterScreen(
    navController: NavController,
    viewModel: StepCounterViewModel = viewModel(factory = StepCounterViewModel.Factory)
) {
    val todayData by viewModel.todaySteps.collectAsState()
    val history by viewModel.historySteps.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Schrittzähler") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                StepProgressRing(
                    steps = todayData?.steps ?: 0,
                    target = todayData?.target ?: 10000
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                StepStatsRow(todayData)
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text(
                    "Vergangene 7 Tage",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(history) { entry ->
                HistoryItem(entry)
            }
        }
    }
}

@Composable
fun StepProgressRing(steps: Int, target: Int) {
    val progress = if (target > 0) steps.toFloat() / target else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000)
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.size(200.dp)) {
            // Background Circle
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                style = Stroke(width = 20.dp.toPx())
            )
            // Progress Arc
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = steps.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "von $target",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StepStatsRow(data: DailyStepData?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Kilometer", value = String.format("%.2f", (data?.distanceMeters ?: 0) / 1000f))
        StatItem(label = "Kalorien", value = "${data?.caloriesBurned ?: 0} kcal")
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun HistoryItem(entry: DailyStepData) {
    val dateFormat = SimpleDateFormat("EEE, dd. MMM", Locale.GERMANY)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(dateFormat.format(entry.date), fontWeight = FontWeight.Medium)
                Text("${entry.steps} Schritte", color = Color.Gray)
            }
            Text(
                "${((entry.steps.toFloat() / entry.target) * 100).toInt()}%",
                fontWeight = FontWeight.Bold,
                color = if (entry.steps >= entry.target) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
            )
        }
    }
}
