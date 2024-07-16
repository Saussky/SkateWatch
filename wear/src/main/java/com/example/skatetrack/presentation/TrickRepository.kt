package com.example.skatetrack.data.com.example.skatetrack.presentation
import com.example.skatetrack.presentation.Trick

object TrickRepository {
    fun getAllTricks(): List<Trick> {
        return listOf(
            Trick("Regular", "Ollie", 1),
            Trick("Switch", "Kickflip", 1),
            Trick("Nollie", "Heelflip", 1)
        )
    }
}