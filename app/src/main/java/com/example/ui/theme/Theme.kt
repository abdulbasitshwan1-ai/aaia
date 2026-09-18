package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceCard,
    onPrimaryContainer = TextPrimary,
    secondary = AccentPurple,
    onSecondary = TextPrimary,
    tertiary = AccentCyan,
    background = ObsidianBg,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = StatusError,
    onError = TextPrimary,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryBlue,
    secondary = AccentPurple,
    tertiary = AccentCyan,
    background = ObsidianBg,
    surface = SurfaceDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
  )

@Composable
fun BasokaTheme(
  darkTheme: Boolean = true, // Default to Dark Premium for BASOKA
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content,
    )
  }
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  BasokaTheme(darkTheme, dynamicColor, content)
}


