package com.example.skatetrack.presentation

//class MainActivitypackage com.example.skatetrack.presentation

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
import java.io.File
import java.io.FileWriter
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            WearApp { tricks -> writeCSV(tricks) }
        }
    }

    private fun writeCSV(tricks: List<Trick>) {
        val csvHeader = "Trick,Attempts,Lands\n"
        val csvBody = StringBuilder()
        for (trick in tricks) {
            csvBody.append("${trick.name},${trick.attempts},${trick.lands}\n")
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
}

@Composable
fun WearApp(onExport: (List<Trick>) -> Unit) {
    SkateTrackTheme {
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
                text = "Attempts: ${currentTrick.attempts}",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Lands: ${currentTrick.lands}",
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
                    currentTrick.lands++
                    currentTrick.attempts++
                    if (currentTrick.lands >= currentTrick.targetLands) {
                        if (currentTrickIndex < tricks.size - 1) {
                            currentTrickIndex++
                        }
                    }
                }) {
                    Text("Land")
                }
                Button(onClick = {
                    currentTrick.attempts++
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
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp {}
}