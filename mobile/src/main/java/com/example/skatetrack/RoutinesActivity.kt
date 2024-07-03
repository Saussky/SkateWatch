package com.example.skatetrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoutinesActivity : AppCompatActivity() {

    private lateinit var routineNameInput: EditText
    private lateinit var stanceSpinner: Spinner
    private lateinit var trickSpinner: Spinner
    private lateinit var landingGoalInput: EditText
    private lateinit var addRoutineButton: Button
    private lateinit var saveRoutineButton: Button
    private lateinit var routinesRecyclerView: RecyclerView
    private val routines = mutableListOf<Trick>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routines)

        routineNameInput = findViewById(R.id.routineNameInput)
        stanceSpinner = findViewById(R.id.stanceSpinner)
        trickSpinner = findViewById(R.id.trickSpinner)
        landingGoalInput = findViewById(R.id.landingGoalInput)
        addRoutineButton = findViewById(R.id.addRoutineButton)
        saveRoutineButton = findViewById(R.id.saveRoutineButton)
        routinesRecyclerView = findViewById(R.id.routinesRecyclerView)

        setupSpinners()
        setupRecyclerView()
        loadSavedRoutines()

        addRoutineButton.setOnClickListener {
            addRoutine()
        }

        saveRoutineButton.setOnClickListener {
            saveRoutine()
        }
    }

    private fun setupSpinners() {
        val stances = listOf("Reg", "Switch", "Nollie", "Fakie")
        val tricks = listOf(
            "Ollie", "Shuv", "F Shuv", "B 180", "F 180", "Kickflip", "Heelflip", "B Flip", "F Flip", "B Heel", "F Heel", "Varial Flip", "Varial Heel", "B Bigspin", "F Bigspin", "Tre Flip",
            "Hardflip", "Inward Heelflip", "Laser Flip", "Impossible", "Bigsflip"
        )

        val stanceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, stances)
        stanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        stanceSpinner.adapter = stanceAdapter

        val trickAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tricks)
        trickAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trickSpinner.adapter = trickAdapter
    }

    private fun setupRecyclerView() {
        routinesRecyclerView.layoutManager = LinearLayoutManager(this)
        routinesRecyclerView.adapter = RoutineAdapter(routines) { position ->
            deleteRoutine(position)
        }
    }

    private fun addRoutine() {
        val stance = stanceSpinner.selectedItem.toString()
        val trick = trickSpinner.selectedItem.toString()
        val landingGoal = landingGoalInput.text.toString().toIntOrNull() ?: 0

        if (landingGoal > 0) {
            val routine = Trick(stance, trick, landingGoal)
            routines.add(routine)
            routinesRecyclerView.adapter?.notifyDataSetChanged()
            landingGoalInput.text.clear()
        }
    }

    private fun deleteRoutine(position: Int) {
        routines.removeAt(position)
        routinesRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun saveRoutine() {
        val routineName = routineNameInput.text.toString()
        if (routineName.isEmpty()) {
            routineNameInput.error = "Routine name is required"
            return
        }

        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        val routinesList = loadSavedRoutines().toMutableList()
        routinesList.add(Routine(routineName, routines.toList()))
        val json = gson.toJson(routinesList)

        Log.d("RoutinesActivity", "Saving routine: $json")
        editor.putString("routines_list", json)
        editor.apply()

        val intent = Intent(this, RoutinesSelectionActivity::class.java)
        startActivity(intent)
    }

    private fun loadSavedRoutines(): List<Routine> {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}
