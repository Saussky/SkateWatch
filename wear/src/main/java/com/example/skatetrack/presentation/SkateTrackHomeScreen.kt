package com.example.skatetrack.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkateTrackHomeScreen(
    onRoutinesSelected: () -> Unit,
    onTricksSelected: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRoutinesSelected, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Routines")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTricksSelected, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Tricks")
        }
    }
}