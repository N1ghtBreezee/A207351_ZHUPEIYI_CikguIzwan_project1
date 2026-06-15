package com.example.a207351_cikguizwan_lab5.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6EE27B),
    secondary = Color(0xFFB7CCB4),
    tertiary = Color(0xFFA0CFD7),
    background = Color(0xFF1A1C19),
    surface = Color(0xFF1E3A5F)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006E24),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF8AFF98),
    secondary = Color(0xFF50634F),
    background = Color(0xFFFCFDF6),
    surface = Color(0xFFE0FFE4)
)

@Composable
fun A207351_cikguizwan_lab5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}