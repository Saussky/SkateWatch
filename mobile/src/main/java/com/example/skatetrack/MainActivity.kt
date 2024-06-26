package com.example.skatetrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity() {

    private val TAG = "SkateTrackMobile"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupWearable()

        findViewById<Button>(R.id.button_routines).setOnClickListener {
            val intent = Intent(this, RoutinesSelectionActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button_stats).setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupWearable() {
        checkConnectedNodes()
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
}