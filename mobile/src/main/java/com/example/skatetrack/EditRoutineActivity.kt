package com.example.skatetrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditRoutineActivity : AppCompatActivity() {

    private lateinit var routineName: String
    private lateinit var tricksRecyclerView: RecyclerView
    private lateinit var saveButton: Button
    private val tricks = mutableListOf<Trick>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_routine)

        routineName = intent.getStringExtra("routineName") ?: ""
        tricksRecyclerView = findViewById(R.id.tricksRecyclerView)
        saveButton = findViewById(R.id.saveButton)

        setupRecyclerView()
        loadRoutine()

        saveButton.setOnClickListener {
            saveRoutine()
        }
    }

    private fun setupRecyclerView() {
        tricksRecyclerView.layoutManager = LinearLayoutManager(this)
        tricksRecyclerView.adapter = TrickAdapter(tricks, this::deleteTrick, this::moveTrickUp)
    }

    private fun loadRoutine() {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        val routine = routines.find { it.name == routineName }
        routine?.let {
            tricks.clear()
            tricks.addAll(it.tricks)
            tricksRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

    private fun saveRoutine() {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: MutableList<Routine> = gson.fromJson(json, type) ?: mutableListOf()

        val routineIndex = routines.indexOfFirst { it.name == routineName }
        if (routineIndex != -1) {
            routines[routineIndex] = Routine(routineName, tricks,)
        }

        val updatedJson = gson.toJson(routines)
        editor.putString("routines_list", updatedJson)
        editor.apply()

        val intent = Intent(this, RoutinesSelectionActivity::class.java)
        startActivity(intent)
    }

    private fun deleteTrick(position: Int) {
        tricks.removeAt(position)
        tricksRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun moveTrickUp(position: Int) {
        if (position > 0) {
            val trick = tricks.removeAt(position)
            tricks.add(position - 1, trick)
            tricksRecyclerView.adapter?.notifyDataSetChanged()
        }
    }
}
