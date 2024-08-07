package com.example.skatetrack

import java.sql.Time
import java.util.Date

data class NoLand(
    val date: Date,
    val time: Time,
    val speed: Double
)

data class Land(
    val date: Date,
    val time: Time,
    val speed: Double
)

data class Trick(
    val stance: String,
    val trick: String,
    val landingGoal: Int,
    val lands: MutableList<Land> = mutableListOf(),
    val noLands: MutableList<NoLand> = mutableListOf()
)
