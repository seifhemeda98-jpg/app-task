package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Available Theme Accents for Customization with iOS 26 Glass Aesthetics
enum class ThemeAccent(
    val title: String,
    val subtitle: String,
    val primary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val glowColor: Color,
    val gradientColors: List<Color>,
    val emoji: String
) {
    SAPPHIRE(
        title = "ياقوت ملكي",
        subtitle = "Sapphire Blue",
        primary = Color(0xFF2563EB),
        primaryContainer = Color(0xFFDBEAFE),
        secondary = Color(0xFF38BDF8),
        glowColor = Color(0x663B82F6),
        gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8), Color(0xFF1E3A8A)),
        emoji = "🔷"
    ),
    EMERALD(
        title = "زمردي زجاجي",
        subtitle = "Cyber Emerald",
        primary = Color(0xFF059669),
        primaryContainer = Color(0xFFD1FAE5),
        secondary = Color(0xFF34D399),
        glowColor = Color(0x6610B981),
        gradientColors = listOf(Color(0xFF10B981), Color(0xFF047857), Color(0xFF064E3B)),
        emoji = "🟢"
    ),
    AMETHYST(
        title = "أميثيست بنفسجي",
        subtitle = "Vision Purple",
        primary = Color(0xFF7C3AED),
        primaryContainer = Color(0xFFEDE9FE),
        secondary = Color(0xFFA78BFA),
        glowColor = Color(0x668B5CF6),
        gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9), Color(0xFF4C1D95)),
        emoji = "🔮"
    ),
    SUNSET(
        title = "غروب ذهبي",
        subtitle = "Sunset Amber",
        primary = Color(0xFFEA580C),
        primaryContainer = Color(0xFFFFEDD5),
        secondary = Color(0xFFFBBF24),
        glowColor = Color(0x66F97316),
        gradientColors = listOf(Color(0xFFF97316), Color(0xFFC2410C), Color(0xFF7C2D12)),
        emoji = "🌅"
    ),
    ROSE(
        title = "وردي كوارتز",
        subtitle = "Rose Glass",
        primary = Color(0xFFE11D48),
        primaryContainer = Color(0xFFFFE4E6),
        secondary = Color(0xFFFB7185),
        glowColor = Color(0x66F43F5E),
        gradientColors = listOf(Color(0xFFF43F5E), Color(0xFFBE123C), Color(0xFF881337)),
        emoji = "🌸"
    ),
    OCEAN(
        title = "محيطي نيون",
        subtitle = "Ocean Cyan",
        primary = Color(0xFF0891B2),
        primaryContainer = Color(0xFFCFFAFE),
        secondary = Color(0xFF22D3EE),
        glowColor = Color(0x6606B6D4),
        gradientColors = listOf(Color(0xFF06B6D4), Color(0xFF0E7490), Color(0xFF164E63)),
        emoji = "🌊"
    )
}

// Liquid Glass Palette Colors
val GlassWhite85 = Color(0xD9FFFFFF)
val GlassWhite70 = Color(0xB3FFFFFF)
val GlassWhite50 = Color(0x80FFFFFF)
val GlassWhite20 = Color(0x33FFFFFF)
val GlassWhite10 = Color(0x1AFFFFFF)

val GlassDark85 = Color(0xD9131D31)
val GlassDark60 = Color(0x991E293B)
val GlassDark30 = Color(0x4D334155)

// Shimmering and Specular Borders for Liquid Glass
fun getLiquidGlassBorder(isDark: Boolean = false): Brush {
    return if (isDark) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.40f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.25f)
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.95f),
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.80f)
            )
        )
    }
}

// Subject Accent Colors for student badges
val SubjectColors = listOf(
    Color(0xFF2563EB), // Blue
    Color(0xFF059669), // Emerald
    Color(0xFFD97706), // Amber
    Color(0xFF7C3AED), // Amethyst
    Color(0xFFE11D48), // Rose
    Color(0xFF0891B2), // Ocean
    Color(0xFFEA580C), // Sunset
    Color(0xFF4F46E5)  // Indigo
)
