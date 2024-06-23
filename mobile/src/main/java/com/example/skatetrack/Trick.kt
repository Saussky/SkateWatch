package com.example.skatetrack

import java.sql.Time
import java.util.Date

data class Attempt(
    val date: Date,
    val time: Time
)

data class Land(
    val date: Date,
    val time: Time
)

data class Trick(
    val stance: String,
    val trick: String,
    val landingGoal: Int,
    val lands: MutableList<Land> = mutableListOf(),
    val attempts: MutableList<Attempt> = mutableListOf()
)
