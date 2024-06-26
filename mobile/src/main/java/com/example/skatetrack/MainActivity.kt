package com.example.skatetrack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
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
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient
    private val TAG = "SkateTrackMobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWearable()

        findViewById<Button>(R.id.button_routines).setOnClickListener {
            val intent = Intent(this, RoutinesSelectionActivity::class.java)
            startActivity(intent)
        }
    }


    private fun setupWearable() {
        dataClient = Wearable.getDataClient(this)
        checkConnectedNodes()
        dataClient.addListener(this)
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

    override fun onResume() {
        super.onResume()
        if (::dataClient.isInitialized) {
            dataClient.addListener(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::dataClient.isInitialized) {
            dataClient.removeListener(this)
        }
    }



    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received MAINACTIVITY")
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "Data path: ${dataItem.uri.path}")
            }
        }
    }
}
