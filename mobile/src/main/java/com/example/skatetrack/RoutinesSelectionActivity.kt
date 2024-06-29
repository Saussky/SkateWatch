package com.example.skatetrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoutinesSelectionActivity : AppCompatActivity() {

    private lateinit var createRoutineButton: Button
    private lateinit var routinesRecyclerView: RecyclerView
    private val routines = mutableListOf<Routine>()
    private val TAG = "SkateTrackMobile"
    private val PREFS_NAME = "routines"
    private val ROUTINES_KEY = "routines_list"

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

        val sendRoutinesButton: Button = findViewById(R.id.send_routines_button)
        sendRoutinesButton.setOnClickListener {
            sendRoutinesToWearable()
        }

        val clearDataButton: Button = findViewById(R.id.button_clear_data)
        clearDataButton.setOnClickListener {
            clearSharedPreferences("routines")
            clearSharedPreferences("routine_history")
        }
    }

    private fun clearSharedPreferences(prefName: String) {
        val sharedPreferences = getSharedPreferences(prefName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
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
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(ROUTINES_KEY, null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val savedRoutines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        Log.d(TAG, "Saved routines: $savedRoutines")


        routines.clear()
        routines.addAll(savedRoutines)
        routinesRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun confirmDeleteRoutine(position: Int) {
        val routine = routines[position]
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        routines.removeAt(position)
        val json = gson.toJson(routines)
        editor.putString(ROUTINES_KEY, json)
        editor.apply()

        routinesRecyclerView.adapter?.notifyItemRemoved(position)
    }

    private fun sendRoutinesToWearable() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(ROUTINES_KEY, null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        Log.d(TAG, "Sending routines: $routines")

        val routinesJson = gson.toJson(routines)
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/routines").apply {
            dataMap.putString("routines", routinesJson)
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq).addOnSuccessListener {
            Log.d(TAG, "Routines sent to wearable")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send routines", it)
        }
    }

}