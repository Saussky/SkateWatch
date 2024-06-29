package com.example.skatetrack

import java.util.Date

data class Routine(
    val name: String,
    val tricks: List<Trick>,
    val startTime: Date? = null,
    var endTime: Date? = null
)