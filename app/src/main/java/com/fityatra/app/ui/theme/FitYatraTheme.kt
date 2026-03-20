package com.fityatra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF388E3C),
    secondary = androidx.compose.ui.graphics.Color(0xFF1976D2),
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF66BB6A),
    secondary = androidx.compose.ui.graphics.Color(0xFF64B5F6),
    background = androidx.compose.ui.graphics.Color(0xFF121212)
)

@Composable
fun FitYatraTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
