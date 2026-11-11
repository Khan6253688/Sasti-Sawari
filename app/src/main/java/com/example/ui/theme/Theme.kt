package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = DarkEmeraldOnPrimary,
    primaryContainer = DarkEmeraldContainer,
    onPrimaryContainer = DarkEmeraldOnContainer,
    secondary = DarkGold,
    onSecondary = DarkLuxuryBackground,
    secondaryContainer = DarkGoldContainer,
    onSecondaryContainer = DarkGold,
    tertiary = DarkGold,
    onTertiary = DarkLuxuryBackground,
    background = DarkLuxuryBackground,
    surface = DarkLuxurySurface,
    surfaceVariant = DarkLuxurySurfaceVariant,
    onBackground = DarkLuxuryOnSurface,
    onSurface = DarkLuxuryOnSurface,
    onSurfaceVariant = DarkLuxuryOnSurfaceVariant,
    outline = DarkLuxuryOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldPrimaryContainer,
    onPrimaryContainer = EmeraldOnPrimaryContainer,
    secondary = MidnightSecondary,
    onSecondary = MidnightOnSecondary,
    secondaryContainer = MidnightSecondaryContainer,
    onSecondaryContainer = MidnightOnSecondaryContainer,
    tertiary = ImperialGold,
    onTertiary = GoldOnTertiary,
    tertiaryContainer = GoldTertiaryContainer,
    onTertiaryContainer = GoldOnTertiaryContainer,
    background = LuxuryBackground,
    surface = LuxurySurface,
    surfaceVariant = LuxurySurfaceVariant,
    onBackground = LuxuryOnSurface,
    onSurface = LuxuryOnSurface,
    onSurfaceVariant = LuxuryOnSurfaceVariant,
    outline = LuxuryOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
