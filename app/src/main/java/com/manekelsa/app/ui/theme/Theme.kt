package com.manekelsa.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OrangePrimary  = Color(0xFFE65100)
val OrangeLight    = Color(0xFFFF8A50)
val OrangeDark     = Color(0xFFAC1900)
val GreenAvailable = Color(0xFF2E7D32)
val RedUnavailable = Color(0xFFC62828)

private val LightColorScheme = lightColorScheme(
    primary          = OrangePrimary,
    onPrimary        = Color.White,
    primaryContainer = OrangeLight,
    secondary        = OrangeDark,
    background       = Color(0xFFFFF8F0),
    surface          = Color.White,
    onBackground     = Color(0xFF1C1B1F),
    onSurface        = Color(0xFF1C1B1F),
)

@Composable
fun ManeKelsaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColorScheme, content = content)
}