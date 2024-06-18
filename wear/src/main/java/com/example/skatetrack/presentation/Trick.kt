package com.example.skatetrack.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class Trick(
    val name: String,
    var attempts: Int = 0,
    var lands: Int = 0,
    val targetLands: Int = 3
) {
    var attemptsState by mutableStateOf(attempts)
    var landsState by mutableStateOf(lands)
}
