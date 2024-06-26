package com.example.skatetrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log


@Composable
fun SkateTrackWearApp(routines: List<Routine>, logAttempt: (Int, Int, Boolean) -> Unit) {
    var currentRoutineIndex by remember { mutableStateOf<Int?>(null) }
    var currentTrickIndex by remember { mutableStateOf(0) }
    val currentRoutine = currentRoutineIndex?.let { routines.getOrNull(it) }
    val currentTrick = currentRoutine?.tricks?.getOrNull(currentTrickIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SkateTrack") }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentRoutine == null) {
                    Text("Select a Routine")
                    Spacer(modifier = Modifier.height(16.dp))
                    routines.forEachIndexed { index, routine ->

                        Button(onClick = { currentRoutineIndex = index }) {
                            Text(routine.name)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (currentTrick != null) {
                    Text(text = "Trick: ${currentTrick.trick}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Lands: ${currentTrick.lands.size}/${currentTrick.landingGoal}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = { logAttempt(currentRoutineIndex!!, currentTrickIndex, false) }) {
                            Text("No Land")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = { logAttempt(currentRoutineIndex!!, currentTrickIndex, true) }) {
                            Text("Land")
                        }
                    }
                } else {
                    Text(text = "No routine available")
                }
            }
        }
    )
}
