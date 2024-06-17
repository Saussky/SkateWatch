package com.example.skatetrack.presentation

data class Trick(
    val name: String,
    var attempts: Int = 0,
    var lands: Int = 0,
    val targetLands: Int = 3
)
