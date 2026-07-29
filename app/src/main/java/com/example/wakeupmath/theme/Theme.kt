package com.example.wakeupmath.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WakeUpMathColorScheme = darkColorScheme(
    primary = AccentRed,
    onPrimary = Color.White,
    primaryContainer = AccentRedDark,
    onPrimaryContainer = Color.White,
    secondary = DeepBlue,
    onSecondary = TextPrimary,
    secondaryContainer = CardSurface,
    onSecondaryContainer = TextPrimary,
    tertiary = SuccessGreen,
    onTertiary = Color.Black,
    background = Charcoal,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = DividerColor,
    outlineVariant = DividerColor,
)

@Composable
fun WakeUpMathTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = WakeUpMathColorScheme,
        typography = Typography,
        content = content,
    )
}
