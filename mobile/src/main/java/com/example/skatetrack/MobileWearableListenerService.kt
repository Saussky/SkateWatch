package com.example.skatetrack

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MobileWearableListenerService : WearableListenerService() {
    private val TAG = "MobileWearService"

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "Data path: ${dataItem.uri.path}")
                when (dataItem.uri.path) {
                    "/update_routines" -> {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        val routinesJson = dataMap.getString("routines")
                        Log.d(TAG, "Received routines JSON: $routinesJson")
                        val type = object : TypeToken<List<Routine>>() {}.type
                        val updatedRoutines: List<Routine> = Gson().fromJson(routinesJson, type) ?: emptyList()
                        Log.d(TAG, "Parsed routines: $updatedRoutines")

                        saveRoutinesToPreferences(updatedRoutines)
                    }
                    "/get_routines" -> {
                        sendRoutinesToWearable()
                    }
                }
            }
        }
    }

    private fun saveRoutinesToPreferences(routines: List<Routine>) {
        val sharedPreferences = getSharedPreferences("routines", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val routinesJson = Gson().toJson(routines)
        editor.putString("routines_list", routinesJson)
        editor.apply()
        Log.d(TAG, "Routines saved to preferences: $routines")
    }

    private fun sendRoutinesToWearable() {
        val sharedPreferences = getSharedPreferences("routines", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

        val routinesJson = gson.toJson(routines)
        val putDataReq = PutDataMapRequest.create("/routines").apply {
            dataMap.putString("routines", routinesJson)
        }.asPutDataRequest()

        Wearable.getDataClient(this).putDataItem(putDataReq).addOnSuccessListener {
            Log.d(TAG, "Routines sent to wearable")
        }.addOnFailureListener {
            Log.e(TAG, "Failed to send routines", it)
        }
    }
}