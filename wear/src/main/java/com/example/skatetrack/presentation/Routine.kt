package com.example.skatetrack.presentation

import java.util.Date

data class Routine(
    val name: String,
    val tricks: List<Trick>,
    val startTime: Date,
    var endTime: Date? = null
)
