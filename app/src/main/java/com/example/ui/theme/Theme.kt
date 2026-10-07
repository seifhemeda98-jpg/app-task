package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accent: ThemeAccent = ThemeAccent.SAPPHIRE,
    content: @Composable () -> Unit
) {
    val lightColors = lightColorScheme(
        primary = accent.primary,
        onPrimary = Color.White,
        primaryContainer = accent.primaryContainer,
        onPrimaryContainer = accent.primary,
        secondary = accent.secondary,
        onSecondary = Color.White,
        secondaryContainer = accent.primaryContainer.copy(alpha = 0.5f),
        onSecondaryContainer = accent.primary,
        tertiary = Color(0xFFD97706),
        onTertiary = Color.White,
        background = Color(0xFFF1F5F9), // Subtle slate background to accentuate liquid glass
        onBackground = Color(0xFF0F172A),
        surface = Color.White,
        onSurface = Color(0xFF0F172A),
        surfaceVariant = Color(0xFFF8FAFC),
        onSurfaceVariant = Color(0xFF475569),
        outline = Color(0xFFCBD5E1)
    )

    val darkColors = darkColorScheme(
        primary = accent.secondary,
        onPrimary = Color(0xFF0F172A),
        primaryContainer = accent.primary.copy(alpha = 0.4f),
        onPrimaryContainer = Color(0xFFE2E8F0),
        secondary = accent.secondary,
        onSecondary = Color(0xFF0F172A),
        background = Color(0xFF090D16),
        onBackground = Color(0xFFF1F5F9),
        surface = Color(0xFF111827),
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = Color(0xFF1E293B),
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = Color(0xFF334155)
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        typography = Typography,
        content = content
    )
}
