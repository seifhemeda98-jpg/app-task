package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GlassDark60
import com.example.ui.theme.GlassDark85
import com.example.ui.theme.GlassWhite70
import com.example.ui.theme.GlassWhite85
import com.example.ui.theme.getLiquidGlassBorder

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(26.dp),
    tintColor: Color? = null,
    glowColor: Color? = null,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val baseGlassColor = if (isDark) {
        tintColor?.copy(alpha = 0.22f) ?: GlassDark85
    } else {
        tintColor?.copy(alpha = 0.16f) ?: GlassWhite85
    }

    val finalGlow = glowColor ?: tintColor?.copy(alpha = 0.25f)

    Box(
        modifier = modifier
            .drawBehind {
                if (finalGlow != null) {
                    drawCircle(
                        color = finalGlow,
                        radius = size.maxDimension * 0.45f,
                        center = Offset(size.width * 0.85f, size.height * 0.15f)
                    )
                }
            }
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .clip(shape)
            .background(baseGlassColor)
            .border(
                width = 1.2.dp,
                brush = getLiquidGlassBorder(isDark),
                shape = shape
            )
    ) {
        content()
    }
}

@Composable
fun AtmosphericBackground(
    accentColor: Color,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF090D16) else Color(0xFFF1F5F9)

    Box(
        modifier = Modifier
            .background(bgColor)
            .drawBehind {
                // Subtle liquid light orbs floating in background
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isDark) 0.28f else 0.18f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.15f, size.height * 0.12f),
                        radius = size.width * 0.75f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = if (isDark) 0.20f else 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.9f, size.height * 0.55f),
                        radius = size.width * 0.65f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = if (isDark) 0.18f else 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.3f, size.height * 0.88f),
                        radius = size.width * 0.6f
                    )
                )
            }
    ) {
        content()
    }
}
