package com.example.skatetrack.presentation

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.ui.Alignment
import kotlinx.coroutines.*

@Composable
fun SkateTrackCurrentTrickScreen(
    currentTrick: Trick?,
    logAttempt: (Int, Int, Boolean) -> Pair<Int, Int>,
    currentRoutineIndex: Int?,
    currentTrickIndexState: MutableState<Int>,
    currentRoutineInstance: MutableState<Routine?>,
    attemptCount: MutableState<Int>,
    lastSpeed: MutableState<Double>,
    onRoutineFinished: () -> Unit
) {
    val currentTrickIndex = currentTrickIndexState.value
    val TAG = "SkateTrackWear"

    // Debounce flag to prevent multiple skipCurrentTrick calls
    var debounce by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun skipCurrentTrick() {
        val currentRoutine = currentRoutineInstance.value ?: return
        val newTrickIndex = currentTrickIndex + 1

        if (newTrickIndex >= currentRoutine.tricks.size) {
            // End routine or no more tricks to skip
            currentRoutineInstance.value = null
            onRoutineFinished()
            return
        }

        // Update the currentTrickIndex state
        currentTrickIndexState.value = newTrickIndex
        Log.d(TAG, "Skipped to next trick: $newTrickIndex")
    }

    if (currentTrick == null) {
        Log.d(TAG, "No current trick, finishing routine")
        onRoutineFinished()
        return
    }

    LaunchedEffect(currentTrickIndex) {
        if (currentRoutineInstance.value?.tricks?.getOrNull(currentTrickIndex) == null) {
            Log.d(TAG, "No more tricks, finishing routine")
            onRoutineFinished()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
        Log.d(TAG, "CURRENT TRICK: $currentTrick")

        Text(text = "${currentTrick.stance} ${currentTrick.trick}", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Lands: ${currentTrick.lands.size}/${currentTrick.landingGoal}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Attempts: ${attemptCount.value}")
        Spacer(modifier = Modifier.height(8.dp))
//        Text(text = "Last Speed: ${String.format("%.2f", lastSpeed.value)} km/h")
//        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                val result = logAttempt(currentRoutineIndex ?: 0, currentTrickIndex, false)
                if (currentTrickIndex != result.second) {
                    attemptCount.value = 0
                    lastSpeed.value = 0.0
                } else {
                    attemptCount.value += 1
                    lastSpeed.value = currentTrick.noLands.lastOrNull()?.speed ?: 0.0
                }
                if (result.second == 0 && currentTrickIndex != result.second) {
                    currentRoutineInstance.value = null
                    onRoutineFinished()
                } else {
                    currentTrickIndexState.value = result.second
                }
            }, modifier = Modifier.weight(1f)) {
                Text("✗", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                val result = logAttempt(currentRoutineIndex ?: 0, currentTrickIndex, true)
                if (currentTrickIndex != result.second) {
                    attemptCount.value = 0
                    lastSpeed.value = 0.0
                } else {
                    attemptCount.value += 1
                    lastSpeed.value = currentTrick.lands.lastOrNull()?.speed ?: 0.0
                }
                if (result.second == 0 && currentTrickIndex != result.second) {
                    currentRoutineInstance.value = null
                    onRoutineFinished()
                } else {
                    currentTrickIndexState.value = result.second
                }
            }, modifier = Modifier.weight(1f)) {
                Text("✓", fontSize = 24.sp)
            }
        }
    }
}