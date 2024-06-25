package com.example.skatetrack

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    private val PERMISSIONS_REQUEST_CODE = 1001

    private val requiredPermissions = arrayOf(
        Manifest.permission.WAKE_LOCK,
        Manifest.permission.INTERNET,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        "com.google.android.providers.gsf.permission.READ_GSERVICES",
        "com.google.android.permission.PROVIDE_BACKGROUND"
    )

    private lateinit var dataClient: DataClient
    private val TAG = "SkateTrackMobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataClient = Wearable.getDataClient(this)

        // Check for connected nodes
        checkConnectedNodes()

        // Listen for data changes
        dataClient.addListener(this)

        findViewById<Button>(R.id.send_message_button).setOnClickListener {
            sendMessageToWearable()
        }

        if (!hasPermissions(requiredPermissions)) {
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSIONS_REQUEST_CODE)
        } else {
            setupWearable()
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun setupWearable() {
        dataClient = Wearable.getDataClient(this)
        checkConnectedNodes()
        dataClient.addListener(this)
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

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(this)
    }

    private fun checkConnectedNodes() {
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener(OnSuccessListener<List<Node>> { nodes ->
                if (nodes.isEmpty()) {
                    Log.d(TAG, "No connected nodes found")
                } else {
                    for (node in nodes) {
                        Log.d(TAG, "Connected node: " + node.displayName)
                    }
                }
            }).addOnFailureListener {
                Log.e(TAG, "Failed to get connected nodes", it)
            }
    }


    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received")
        for (event in dataEvents) {
            Log.d(TAG, "EVENT??: $event")
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "Data path: ${dataItem.uri.path}")
                when (dataItem.uri.path) {
                    "/get_routines" -> {
                        sendRoutinesToWearable()
                    }
                    "/message" -> {
                        val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                        val message = dataMap.getString("message")
                        Log.d(TAG, "Received message: $message")
                        Toast.makeText(this, "Received from watch: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sendRoutinesToWearable() {
        val sharedPreferences = getSharedPreferences("routines", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("routines_list", null)
        val type = object : TypeToken<List<Routine>>() {}.type
        val routines: List<Routine> = gson.fromJson(json, type) ?: emptyList()

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
