package io.tvelu77.freya.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary          = Rose300,
  onPrimary        = Color.White,
  primaryContainer = Rose100,
  onPrimaryContainer = Neutral900,
  secondary        = Lavender300,
  onSecondary      = Color.White,
  secondaryContainer = Lavender100,
  tertiary         = Sage500,
  onTertiary       = Color.White,
  tertiaryContainer = Sage100,
  background       = Neutral50,
  onBackground     = Neutral900,
  surface          = Color.White,
  onSurface        = Neutral900,
  surfaceVariant   = Neutral100,
  outline          = Neutral300,
  error            = Color(0xFFE57373),
  onError          = Color.White
)

private val DarkColorScheme = darkColorScheme(
  primary          = Rose300,
  onPrimary        = Neutral900,
  primaryContainer = Color(0xFF4A1530),
  secondary        = Lavender300,
  background       = Color(0xFF1A1A2E),
  surface          = Color(0xFF16213E),
  onBackground     = Neutral100,
  onSurface        = Neutral100
)

@Composable
fun FreyaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = FreyaTypography,
    content = content
  )
}