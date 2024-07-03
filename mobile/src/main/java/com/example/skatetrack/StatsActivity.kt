package com.example.skatetrack

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StatsActivity : AppCompatActivity() {

    private val TAG = "StatsActivity"
    private lateinit var recyclerViewStats: RecyclerView
    private val tricksStats = mutableListOf<TrickStats>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        recyclerViewStats = findViewById(R.id.recyclerView_stats)
        recyclerViewStats.layoutManager = LinearLayoutManager(this)
        recyclerViewStats.adapter = StatsAdapter(tricksStats)

        loadStats()
    }

    private fun loadStats() {
        val sharedPreferences = getSharedPreferences("routine_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routine_history_list", null)
        if (json == null) {
            Log.e(TAG, "No routine history found")
            return
        }

        val type = object : TypeToken<List<Routine>>() {}.type
        val routineHistory: List<Routine> = gson.fromJson(json, type) ?: emptyList()
        Log.d(TAG, "Loaded routine history: $routineHistory")

        tricksStats.clear()
        routineHistory.forEach { routine ->
            Log.d(TAG, "Processing routine: $routine")
            routine.tricks?.forEach { trick ->
                Log.d(TAG, "Processing trick: $trick")
                val attemptsCount = trick.noLands.size
                val landsCount = trick.lands.size
                val maxSpeedLands = trick.lands.maxOfOrNull { it.speed } ?: 0.0
                val maxSpeedNoLands = trick.noLands.maxOfOrNull { it.speed } ?: 0.0
                val maxSpeed = maxOf(maxSpeedLands, maxSpeedNoLands)
                val existingStats = tricksStats.find { it.trickName == trick.trick && it.stance == trick.stance }
                if (existingStats != null) {
                    existingStats.noLands += attemptsCount
                    existingStats.lands += landsCount
                    existingStats.maxSpeed = maxOf(existingStats.maxSpeed, maxSpeed)
                    Log.d(TAG, "Updated existing stats: $existingStats")
                } else {
                    tricksStats.add(TrickStats(trick.trick, trick.stance, attemptsCount, landsCount, maxSpeed))
                    Log.d(TAG, "Added new stats: ${TrickStats(trick.trick, trick.stance, attemptsCount, landsCount, maxSpeed)}")
                }
            } ?: Log.e(TAG, "No tricks found in routine: $routine")
        }
        Log.d(TAG, "Final stats: $tricksStats")
        recyclerViewStats.adapter?.notifyDataSetChanged()
    }

    data class TrickStats(val trickName: String, val stance: String, var noLands: Int, var lands: Int, var maxSpeed: Double)
}