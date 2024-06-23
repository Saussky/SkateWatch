package com.example.skatetrack

import java.util.Date

data class Attempt(
    val date: Date
)

data class Land(
    val date: Date
)

data class Trick(
    val stance: String,
    val trick: String,
    val landingGoal: Int,
    val lands: MutableList<Land> = mutableListOf(),
    val attempts: MutableList<Attempt> = mutableListOf()
)
