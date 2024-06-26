package com.example.skatetrack.presentation

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import java.util.Date

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient
    private val TAG = "SkateTrackWear"
    private val routines = mutableStateListOf<Routine>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkateTrackWearApp(routines) { routineIndex, trickIndex, landed ->
                val (updatedRoutineIndex, updatedTrickIndex) = logAttempt(routineIndex, trickIndex, landed)
                // update the UI state with the new indices if needed
            }
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
                    val type = object : TypeToken<List<Routine>>() {}.type
                    val fetchedRoutines: List<Routine> = Gson().fromJson(routinesJson, type) ?: emptyList()
                    Log.d(TAG, "Parsed routines: $fetchedRoutines")

                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) {
                            routines.clear()
                            routines.addAll(fetchedRoutines)
                            Log.d(TAG, "Routines updated in UI")
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

        if (landed) {
            trick.lands.add(Land(Date(), Time(System.currentTimeMillis()), speed = 0)) // Speed needs to be captured from sensors
        } else {
            trick.attempts.add(Attempt(Date(), Time(System.currentTimeMillis()), speed = 0))
        }

        var newTrickIndex = trickIndex
        if (trick.lands.size >= trick.landingGoal) {
            newTrickIndex = (trickIndex + 1) % routine.tricks.size
        }

        // Update the mobile app with the new data
        val updatedRoutineJson = Gson().toJson(routines)
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/update_routines").apply {
            dataMap.putString("routines", updatedRoutineJson)
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq).addOnSuccessListener {
            Log.d(TAG, "Updated routines sent to phone")
        }

        return Pair(routineIndex, newTrickIndex)
    }
}
