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

import com.example.skatetrack.data.com.example.skatetrack.presentation.TrickRepository
import java.util.Date

@Composable
fun SkateTrackTricksScreen(
    startTrick: (Trick) -> Unit,
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
                        currentTrickIndexState.value = 0
                        attemptCount.value = 0
                        lastSpeed.value = 0.0
                        currentRoutineInstance.value = Routine(trick.trick, listOf(trick), Date())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("${trick.stance} ${trick.trick}")
                }
            }
        }
    }
}