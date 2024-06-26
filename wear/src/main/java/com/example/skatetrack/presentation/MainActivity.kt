package com.example.skatetrack.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.util.*

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient
    private val TAG = "SkateTrackWear"
    private val routines = mutableStateListOf<Routine>()
    private val PREFS_NAME = "SkateTrackPrefs"
    private val ROUTINES_KEY = "routines"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRoutinesFromPreferences()
        setContent {
            SkateTrackWearApp(routines, ::logAttempt)
        }

        dataClient = Wearable.getDataClient(this)

        // Check for connected nodes
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener(OnSuccessListener<List<Node>> { nodes ->
                for (node in nodes) {
                    Log.d(TAG, "Connected node: " + node.displayName)
                }
            })

        // Listen for data changes
        dataClient.addListener(this)
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
        Log.d(TAG, "onResume: Listener added")
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(this)
        Log.d(TAG, "onPause: Listener removed")
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/routines") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val routinesJson = dataMap.getString("routines")
                    Log.d(TAG, "Received routines JSON: $routinesJson")
                    val type = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<List<Routine>>() {}.type
                    val fetchedRoutines: List<Routine> = Gson().fromJson(routinesJson, type) ?: emptyList()
                    Log.d(TAG, "Parsed routines: $fetchedRoutines")

                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            routines.clear()
                            routines.addAll(fetchedRoutines)
                            saveRoutinesToPreferences(fetchedRoutines)
                            Log.d(TAG, "Routines updated in UI and saved to preferences")
                        }
                    }
                } else if (dataItem.uri.path == "/message") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val message = dataMap.getString("message")
                    Log.d(TAG, "Received message: $message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun logAttempt(routineIndex: Int, trickIndex: Int, landed: Boolean): Pair<Int, Int> {
        val routine = routines[routineIndex]
        val trick = routine.tricks[trickIndex]

        Log.d(TAG, "Logging attempt for Routine Index: $routineIndex, Trick Index: $trickIndex, Landed: $landed")
        Log.d(TAG, "Current Routine: $routine")
        Log.d(TAG, "Current Trick: $trick")

        if (landed) {
            trick.lands.add(Land(Date(), Time(System.currentTimeMillis()), speed = 0)) // Speed needs to be captured from sensors
            Log.d(TAG, "Landed added: ${trick.lands.last()}")
        } else {
            trick.attempts.add(Attempt(Date(), Time(System.currentTimeMillis()), speed = 0))
            Log.d(TAG, "Attempt added: ${trick.attempts.last()}")
        }

        var newTrickIndex = trickIndex
        if (trick.lands.size >= trick.landingGoal) {
            newTrickIndex = (trickIndex + 1) % routine.tricks.size
            Log.d(TAG, "New Trick Index: $newTrickIndex")
        }

        // Update the mobile app with the new data
        val updatedRoutineJson = Gson().toJson(routines)
        Log.d(TAG, "Updated Routines JSON: $updatedRoutineJson")
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/update_routines").apply {
            dataMap.putString("routines", updatedRoutineJson)
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq).addOnSuccessListener {
            Log.d(TAG, "Updated routines sent to phone")
        }

        // Save the updated routines to preferences
        saveRoutinesToPreferences(routines)

        return Pair(routineIndex, newTrickIndex)
    }

    private fun saveRoutinesToPreferences(routines: List<Routine>) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val routinesJson = Gson().toJson(routines)
        editor.putString(ROUTINES_KEY, routinesJson)
        editor.apply()
        Log.d(TAG, "Routines saved to preferences")
    }

    private fun loadRoutinesFromPreferences() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val routinesJson = sharedPreferences.getString(ROUTINES_KEY, null)
        if (!routinesJson.isNullOrEmpty()) {
            val type = object : com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken<List<Routine>>() {}.type
            val loadedRoutines: List<Routine> = Gson().fromJson(routinesJson, type) ?: emptyList()
            routines.addAll(loadedRoutines)
            Log.d(TAG, "Routines loaded from preferences: $loadedRoutines")
        } else {
            Log.d(TAG, "No routines found in preferences")
        }
    }
}