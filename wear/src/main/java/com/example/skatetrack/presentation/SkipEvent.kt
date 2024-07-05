package com.example.skatetrack.presentation

import java.util.Date

data class SkipEvent(
    val routineName: String,
    val trickName: String,
    val skipTime: Date
)