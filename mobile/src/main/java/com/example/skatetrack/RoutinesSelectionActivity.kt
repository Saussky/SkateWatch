package com.example.skatetrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.wearable.Node
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

        val sendMessageButton: Button = findViewById(R.id.send_message_button)
        sendMessageButton.setOnClickListener {
            sendMessageToWearable()
        }
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

    private fun sendRoutinesToWearable() {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
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

    private fun sendMessageToWearable() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/message").apply {
            dataMap.putString("message", "Sent from mobile")
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq).addOnSuccessListener {
            Log.d(TAG, "Message sent to wearable")
            Toast.makeText(this, "Message sent to wearable", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send message", it)
        }
    }
}
