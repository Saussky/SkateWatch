package com.example.skatetrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SkateTrackRoutinesScreen(
    routines: List<Routine>,
    startRoutine: (Int) -> Unit,
    currentTrickIndexState: MutableState<Int>,
    currentRoutineInstance: MutableState<Routine?>,
    attemptCount: MutableState<Int>,
    lastSpeed: MutableState<Double>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Routines", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
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
                        currentTrickIndexState.value = 0
                        attemptCount.value = 0
                        lastSpeed.value = 0.0
                        currentRoutineInstance.value = routine
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(routine.name)
                }
            }
        }
    }
}