package com.example.skatetrack

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RoutinesActivity : AppCompatActivity() {

    private lateinit var stanceSpinner: Spinner
    private lateinit var trickSpinner: Spinner
    private lateinit var landingGoalInput: EditText
    private lateinit var addRoutineButton: Button
    private lateinit var routinesRecyclerView: RecyclerView
    private val routines = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routines)

        stanceSpinner = findViewById(R.id.stanceSpinner)
        trickSpinner = findViewById(R.id.trickSpinner)
        landingGoalInput = findViewById(R.id.landingGoalInput)
        addRoutineButton = findViewById(R.id.addRoutineButton)
        routinesRecyclerView = findViewById(R.id.routinesRecyclerView)

        setupSpinners()
        setupRecyclerView()
        addRoutineButton.setOnClickListener {
            addRoutine()
        }
    }

    private fun setupSpinners() {
        val stances = listOf("Reg", "Switch", "Nollie", "Fakie")
        val tricks = listOf(
            "Ollie", "Kickflip", "Heelflip", "Pop Shuvit", "Varial Flip", "Tre Flip",
            "Hardflip", "Inward Heelflip", "Laser Flip", "Impossible", "Bigspin Flip"
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
        routinesRecyclerView.adapter = RoutineAdapter(routines)
    }

    private fun addRoutine() {
        val stance = stanceSpinner.selectedItem.toString()
        val trick = trickSpinner.selectedItem.toString()
        val landingGoal = landingGoalInput.text.toString().toIntOrNull() ?: 0

        if (landingGoal > 0) {
            val routine = Routine(stance, trick, landingGoal)
            routines.add(routine)
            routinesRecyclerView.adapter?.notifyDataSetChanged()
            landingGoalInput.text.clear()
        }
    }
}
