package com.example.skatetrack.presentation

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.skatetrack.data.com.example.skatetrack.presentation.TrickRepository
import kotlinx.coroutines.*
@Composable
fun SkateTrackWearApp(
    routines: List<Routine>,
    logAttempt: (Int, Int, Boolean) -> Pair<Int, Int>,
    startRoutine: (Int) -> Unit,
    startTrick: (Trick) -> Unit,
    currentRoutineInstance: MutableState<Routine?>,
    skipCurrentTrick: () -> Unit,
    currentTrickIndexState: MutableState<Int>,
    lastSpeed: MutableState<Double>
) {
    var currentRoutineIndex by remember { mutableStateOf<Int?>(null) }
    val currentTrickIndex = currentTrickIndexState
    val currentRoutine = currentRoutineInstance.value

    // Debounce flag to prevent multiple skipCurrentTrick calls
    var debounce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Ensure the current trick index is updated when the current routine changes
    LaunchedEffect(currentRoutine) {
        if (currentRoutine != null) {
            currentTrickIndex.value = currentTrickIndex.value.coerceAtMost(currentRoutine.tricks.size - 1)
        } else {
            currentTrickIndex.value = 0
        }
        Log.d("SkateTrackWearApp", "Routine changed, currentTrickIndex: ${currentTrickIndex.value}")
    }

    val currentTrick by remember(currentRoutine, currentTrickIndex.value) {
        derivedStateOf { currentRoutine?.tricks?.getOrNull(currentTrickIndex.value) }
    }

    val attemptCount = remember { mutableStateOf(0) }
    val TAG = "SkateTrackWear"

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(8.dp)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            if (dragAmount < -30 && !debounce) {
                                debounce = true
                                skipCurrentTrick()
                                change.consume()
                                scope.launch {
                                    delay(300)
                                    debounce = false
                                }
                            }
                        }
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentRoutine == null) {
                    Text("Routines", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(imageVector = Icons.Default.ArrowDropUp, contentDescription = "Scroll up", tint = Color.Gray)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(routines) { routine ->
                            Button(
                                onClick = {
                                    val index = routines.indexOf(routine)
                                    startRoutine(index)
                                    currentRoutineIndex = index
                                    currentTrickIndex.value = 0
                                    attemptCount.value = 0
                                    lastSpeed.value = 0.0
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(routine.name)
                            }
                        }
                    }
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Scroll down", tint = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tricks", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(TrickRepository.getAllTricks()) { trick ->
                            Button(
                                onClick = {
                                    startTrick(trick)
                                    currentRoutineIndex = null
                                    currentTrickIndex.value = 0
                                    attemptCount.value = 0
                                    lastSpeed.value = 0.0
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text("${trick.stance} ${trick.trick}")
                            }
                        }
                    }
                } else {
                    currentTrick?.let { trick ->
                        Log.d(TAG, "CURRENT TRICK: $trick")

                        Text(text = "${trick.stance} ${trick.trick}", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Lands: ${trick.lands.size}/${trick.landingGoal}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Attempts: ${attemptCount.value}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Last Speed: ${String.format("%.2f", lastSpeed.value)} km/h")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(onClick = {
                                val result = logAttempt(currentRoutineIndex ?: 0, currentTrickIndex.value, false)
                                if (currentTrickIndex.value != result.second) {
                                    attemptCount.value = 0
                                    lastSpeed.value = 0.0
                                } else {
                                    attemptCount.value += 1
                                    lastSpeed.value = trick.noLands.lastOrNull()?.speed ?: 0.0
                                }
                                if (result.second == 0 && currentTrickIndex.value != result.second) {
                                    currentRoutineInstance.value = null
                                } else {
                                    currentTrickIndex.value = result.second
                                }
                            }, modifier = Modifier.weight(1f)) {
                                Text("✗", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val result = logAttempt(currentRoutineIndex ?: 0, currentTrickIndex.value, true)
                                if (currentTrickIndex.value != result.second) {
                                    attemptCount.value = 0
                                    lastSpeed.value = 0.0
                                } else {
                                    attemptCount.value += 1
                                    lastSpeed.value = trick.lands.lastOrNull()?.speed ?: 0.0
                                }
                                if (result.second == 0 && currentTrickIndex.value != result.second) {
                                    currentRoutineInstance.value = null
                                } else {
                                    currentTrickIndex.value = result.second
                                }
                            }, modifier = Modifier.weight(1f)) {
                                Text("✓", fontSize = 24.sp)
                            }
                        }
                    } ?: Text(text = "No trick available")
                }
            }
        }
    )
}