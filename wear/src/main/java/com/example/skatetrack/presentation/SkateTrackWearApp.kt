package com.example.skatetrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log

@Composable
fun SkateTrackWearApp(
    routines: List<Routine>,
    logAttempt: (Int, Int, Boolean) -> Pair<Int, Int>,
    startRoutine: (Int) -> Unit,
    currentRoutineInstance: MutableState<Routine?>
) {
    var currentRoutineIndex by remember { mutableStateOf<Int?>(null) }
    var currentTrickIndex by remember { mutableStateOf(0) }
    val currentRoutine = currentRoutineInstance.value
    val currentTrick = remember(currentRoutine, currentTrickIndex) { currentRoutine?.tricks?.getOrNull(currentTrickIndex) }
    val attemptCount = remember { mutableStateOf(0) }
    val TAG = "SkateTrackWear"

    Log.d(TAG, "ROUTINESSSS: $routines")

    Scaffold(
        topBar = {
            if (currentRoutineIndex == null) {
                TopAppBar(
                    title = { Text("SkateTrack") }
                )
            }
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
                        Log.d(TAG, "ROUTINE?: ${routine}")

                        Button(onClick = {
                            startRoutine(index)
                            currentRoutineIndex = index
                            currentTrickIndex = 0
                            attemptCount.value = 0
                        }) {
                            Text(routine.name)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else if (currentTrick != null) {
                    Text(text = currentTrick.trick, fontSize = 24.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Lands: ${currentTrick.lands.size}/${currentTrick.landingGoal}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Attempts: ${attemptCount.value}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            val result = logAttempt(currentRoutineIndex!!, currentTrickIndex, false)
                            if (currentTrickIndex != result.second) {
                                attemptCount.value = 0
                            } else {
                                attemptCount.value += 1
                            }
                            if (result.second == 0 && currentTrickIndex != result.second) {
                                currentRoutineIndex = null
                                currentTrickIndex = 0
                                currentRoutineInstance.value = null
                            } else {
                                currentRoutineIndex = result.first
                                currentTrickIndex = result.second
                            }
                        }, modifier = Modifier.weight(1f)) {
                            Text("✗", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val result = logAttempt(currentRoutineIndex!!, currentTrickIndex, true)
                            if (currentTrickIndex != result.second) {
                                attemptCount.value = 0
                            } else {
                                attemptCount.value += 1
                            }
                            if (result.second == 0 && currentTrickIndex != result.second) {
                                currentRoutineIndex = null
                                currentTrickIndex = 0
                                currentRoutineInstance.value = null
                            } else {
                                currentRoutineIndex = result.first
                                currentTrickIndex = result.second
                            }
                        }, modifier = Modifier.weight(1f)) {
                            Text("✓", fontSize = 24.sp)
                        }
                    }
                } else {
                    Text(text = "No routine available")
                }
            }
        }
    )
}