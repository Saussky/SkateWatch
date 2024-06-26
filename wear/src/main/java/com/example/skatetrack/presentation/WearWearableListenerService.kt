package com.example.skatetrack.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.StandardCharsets

class WearWearableListenerService : WearableListenerService() {
    private val TAG = "WearService"

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/toast") {
            val message = String(messageEvent.data, StandardCharsets.UTF_8)
            showToast(message)
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "Data path: ${dataItem.uri.path}")
                when (dataItem.uri.path) {
                    "/routines" -> {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        val routinesJson = dataMap.getString("routines")
                        Log.d(TAG, "Received routines JSON: $routinesJson")
                        val type = object : TypeToken<List<Routine>>() {}.type
                        val fetchedRoutines: List<Routine> = Gson().fromJson(routinesJson, type) ?: emptyList()
                        Log.d(TAG, "Parsed routines: $fetchedRoutines")

                        // Handle routines as needed
                    }
                    "/message" -> {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        val message = dataMap.getString("message")
                        Log.d(TAG, "Received message: $message")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
