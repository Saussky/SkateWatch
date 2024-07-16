// ui/theme/Theme.kt
package com.example.skatetrack.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF1EB980),
    primaryVariant = Color(0xFF045D56),
    secondary = Color(0xFF1EB980),
    background = Color.Black
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF1EB980),
    primaryVariant = Color(0xFF045D56),
    secondary = Color(0xFF1EB980),
    background = Color.White,
    surface = Color.White
)

@Composable
fun SkateTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                content()
            }
        }
    )
}