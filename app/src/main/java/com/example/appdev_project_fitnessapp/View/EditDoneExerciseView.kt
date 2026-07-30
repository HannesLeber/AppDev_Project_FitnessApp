package com.example.appdev_project_fitnessapp.View

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.appdev_project_fitnessapp.Model.DataClasses.ExerciseSet
import com.example.appdev_project_fitnessapp.R
import com.example.appdev_project_fitnessapp.ViewModel.StrengthTrainingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDoneExerciseView(navController: NavHostController, strengthTrainingViewModel: StrengthTrainingViewModel) {
    var doneExerciseToBeEdited = remember { strengthTrainingViewModel.DoneExerciseToBeEdited }!!
    var setIDs by remember { mutableStateOf(doneExerciseToBeEdited.sets) }
    var sets by remember { mutableStateOf(listOf<ExerciseSet>()) }
    val scope = rememberCoroutineScope()
    var doneExerciseToBeEditedName =
        strengthTrainingViewModel.exercises.find { it?.id == doneExerciseToBeEdited.exerciseID }?.name
            ?: stringResource(id = R.string.unknown_exercise)

    LaunchedEffect(Unit) {
        strengthTrainingViewModel.getSetsByIDs(setIDs)
        sets = strengthTrainingViewModel.sets
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    //save Values
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                //save Values
                doneExerciseToBeEdited.sets = setIDs
                strengthTrainingViewModel.updateDoneExercise(doneExerciseToBeEdited)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(doneExerciseToBeEditedName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch(){
                    val setID = strengthTrainingViewModel.insertSet(ExerciseSet(reps= "0" ,weight = "0.0", warmupSet = false))
                    setIDs += setID
                    strengthTrainingViewModel.getSetsByIDs(setIDs)
                    sets = strengthTrainingViewModel.sets
                    Log.d("EditDoneExerciseView", "sets: $sets")
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Training")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(strengthTrainingViewModel.sets, key = { it.id }) { set ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                strengthTrainingViewModel.sets.remove(set)
                                strengthTrainingViewModel.deleteSet(set, doneExerciseToBeEdited)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Color.Red
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 4.dp)
                                    .background(color, shape = CardDefaults.shape),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.delete),
                                        modifier = Modifier.padding(start = 16.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        enableDismissFromEndToStart = false
                    ) {
                        SetItem(
                            doneExerciseToBeEdited.exerciseID.toString(),
                            Icons.Default.Add,
                            set,
                            strengthTrainingViewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun SetItem(
    title: String,
    icon: ImageVector,
    set: ExerciseSet,
    strengthTrainingViewModel: StrengthTrainingViewModel
) {

    var setReps by remember { mutableStateOf(set.reps) }
    var setWeight by remember { mutableStateOf(set.weight) }
    Log.d("SetItem", "set weight to $setWeight")
    var backGroundColor by remember { mutableStateOf(if (set.warmupSet) Color.Green else Color.Gray) }

    val lifecycleOwner = LocalLifecycleOwner.current
    //save Values
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                //save Values
                set.reps = setReps
                set.weight = setWeight
                Log.d("SetItem", "set weight to ${set.weight} from $setWeight")
                strengthTrainingViewModel.updateSet(set)
                Log.d("EditDoneExerciseView", "ON_STOP")
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        //TODO: make clickable and add navigation on click
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = backGroundColor, shape = RoundedCornerShape(16.dp))
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                set.warmupSet = !set.warmupSet
                if (set.warmupSet) {
                    backGroundColor = Color.Green
                } else {
                    backGroundColor = Color.Gray
                }

            },
        contentAlignment = Alignment.Center,
    ) {
        Column() {

            Row() {
                //Reps
                TextField(
                    value = setReps,
                    onValueChange = { setReps = it
                                    Log.d("SetItem", "Reps: $setReps")},
                    textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("x")
                Spacer(modifier = Modifier.width(5.dp))
                //Weight
                TextField(
                    value = setWeight,
                    onValueChange = {
                        setWeight = it
                        Log.d("SetItem", "Weight: $setWeight")},
                    textStyle = TextStyle.Default.copy(fontSize = 28.sp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Text("kg")
            }
        }
    }
}


