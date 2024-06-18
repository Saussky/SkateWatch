package com.example.skatetrack.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.skatetrack.presentation.theme.SkateTrackTheme
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var dataClient: DataClient
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        dataClient = Wearable.getDataClient(this)
        setContent {
            SkateWatchApp { tricks -> writeCSV(tricks) }
        }
    }

    private fun writeCSV(tricks: List<Trick>) {
        val csvHeader = "Trick,Attempts,Lands\n"
        val csvBody = StringBuilder()
        for (trick in tricks) {
            csvBody.append("${trick.name},${trick.attemptsState},${trick.landsState}\n")
        }
        val csvData = csvHeader + csvBody.toString()
        try {
            val file = File(getExternalFilesDir(null), "skate_tricks.csv")
            val writer = FileWriter(file)
            writer.write(csvData)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun sendDataToMobile(tricks: List<Trick>) {
        val dataMap = DataMap().apply {
            putString("tricks_data", tricks.joinToString(separator = "\n") {
                "${it.name},${it.attemptsState},${it.landsState}"
            })
        }
        val putDataMapRequest = PutDataMapRequest.create("/tricks").apply {
            dataMap.putAll(this.dataMap)
        }
        val putDataRequest = putDataMapRequest.asPutDataRequest()
        dataClient.putDataItem(putDataRequest)
    }
}

@Composable
fun SkateWatchApp(onExport: (List<Trick>) -> Unit) {
    var appState by remember { mutableStateOf(AppState.Start) }

    SkateTrackTheme {
        when (appState) {
            AppState.Start -> StartScreen(onStart = { appState = AppState.Tricks })
            AppState.Tricks -> TrickScreen(
                onExport = onExport,
                onFinish = { appState = AppState.Finished }
            )
            AppState.Finished -> StartScreen(onStart = { appState = AppState.Tricks })
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SkateWatch",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onStart) {
            Text("Start")
        }
    }
}

@Composable
fun TrickScreen(onExport: (List<Trick>) -> Unit, onFinish: () -> Unit) {
    val tricks = remember {
        mutableStateListOf(
            Trick("Ollie"),
            Trick("Kickflip"),
            Trick("Heelflip")
        )
    }
    var currentTrickIndex by remember { mutableStateOf(0) }
    val currentTrick = tricks[currentTrickIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = currentTrick.name,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Attempts: ${currentTrick.attemptsState}",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Lands: ${currentTrick.landsState}",
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(onClick = {
                currentTrick.landsState++
                currentTrick.attemptsState++
                if (currentTrick.landsState >= currentTrick.targetLands) {
                    if (currentTrickIndex < tricks.size - 1) {
                        currentTrickIndex++
                    } else {
                        onFinish()
                    }
                }
            }) {
                Text("Land")
            }
            Button(onClick = {
                currentTrick.attemptsState++
            }) {
                Text("No Land")
            }
        }
        Button(onClick = {
            onExport(tricks)
        }) {
            Text("Export to CSV")
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    SkateWatchApp {}
}
