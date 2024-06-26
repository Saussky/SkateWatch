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
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        tricksStats.clear()
        routines.forEach { routine ->
            routine.tricks.forEach { trick ->
                val attemptsCount = trick.attempts.size
                val landsCount = trick.lands.size
                tricksStats.add(TrickStats(trick.trick, attemptsCount, landsCount))
            }
        }
        Log.d(TAG, "Loaded stats: $tricksStats")
        recyclerViewStats.adapter?.notifyDataSetChanged()
    }

    data class TrickStats(val trickName: String, val attempts: Int, val lands: Int)
}