package com.example.skatetrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener {
    private lateinit var dataClient: DataClient
    private val TAG = "SkateTrackMobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataClient = Wearable.getDataClient(this)

        // Check for connected nodes
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener(OnSuccessListener<List<Node>> { nodes ->
                for (node in nodes) {
                    Log.d(TAG, "Connected node: " + node.displayName)
                }
            })

        val routinesButton: Button = findViewById(R.id.button_routines)
        val dataButton: Button = findViewById(R.id.button_data)

        routinesButton.setOnClickListener {
            val intent = Intent(this, RoutinesActivity::class.java)
            startActivity(intent)
        }

        dataButton.setOnClickListener {
            val intent = Intent(this, DataActivity::class.java)
            startActivity(intent)
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

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed event received")
        for (event in dataEvents) {
            Log.d(TAG, "Event type: ${event.type}")
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d(TAG, "DataItem URI: ${dataItem.uri}")
                if (dataItem.uri.path == "/tricks") {
                    Log.d(TAG, "hit endpoint /tricks")
                    val dataMapItem = DataMapItem.fromDataItem(dataItem)
                    val tricksData = dataMapItem.dataMap.getString("tricks_data")
                    Log.d(TAG, "Received data: \n$tricksData")
                    runOnUiThread {
                        // Handle received data if needed
                    }
                } else {
                    Log.d(TAG, "DataItem path does not match /tricks")
                }
            } else {
                Log.d(TAG, "Event type does not match TYPE_CHANGED")
            }
        }
    }
}
