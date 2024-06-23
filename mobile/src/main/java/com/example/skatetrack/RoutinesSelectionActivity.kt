package com.example.skatetrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoutinesSelectionActivity : AppCompatActivity() {

    private lateinit var createRoutineButton: Button
    private lateinit var routinesRecyclerView: RecyclerView
    private val routines = mutableListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routines_selection)

        createRoutineButton = findViewById(R.id.createRoutineButton)
        routinesRecyclerView = findViewById(R.id.routinesRecyclerView)

        createRoutineButton.setOnClickListener {
            val intent = Intent(this, RoutinesActivity::class.java)
            startActivity(intent)
        }

        setupRecyclerView()
        loadSavedRoutines()
    }

    private fun setupRecyclerView() {
        routinesRecyclerView.layoutManager = LinearLayoutManager(this)
        routinesRecyclerView.adapter = RoutineSelectionAdapter(routines) { position ->
            confirmDeleteRoutine(position)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                (routinesRecyclerView.adapter as RoutineSelectionAdapter).notifyItemChanged(viewHolder.adapterPosition)
                confirmDeleteRoutine(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(routinesRecyclerView)
    }

    private fun loadSavedRoutines() {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val savedRoutines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        routines.clear()
        routines.addAll(savedRoutines)
        routinesRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun confirmDeleteRoutine(position: Int) {
        val routine = routines[position]
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        routines.removeAt(position)
        val json = gson.toJson(routines)
        editor.putString("routines_list", json)
        editor.apply()

        routinesRecyclerView.adapter?.notifyItemRemoved(position)
    }
}
