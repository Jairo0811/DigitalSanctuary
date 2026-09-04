package com.jairomatias.digitalsanctuary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = OnSurfaceDark,
    onPrimary = BackgroundEinkDark,
    background = BackgroundEinkDark,
    onBackground = OnSurfaceDark,
    surface = BackgroundEinkDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceContainerLowDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outlineVariant = SurfaceContainerHighestDark,
    outline = OnSurfaceVariantDark,
    secondary = OnSurfaceVariantDark,
    onSecondary = BackgroundEinkDark,
    secondaryContainer = SurfaceContainerHighDark,
    onSecondaryContainer = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLumen,
    onPrimary = OnPrimaryLumen,
    background = BackgroundLumen,
    onBackground = OnSurfaceLight,
    surface = BackgroundLumen,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceContainerLow,
    onSurfaceVariant = OnSurfaceVariantLight,
    outlineVariant = OutlineVariantLumen,
    outline = OutlineLumen,
    secondary = SecondaryLumen,
    onSecondary = OnPrimaryLumen,
    secondaryContainer = SecondaryContainerLumen,
    onSecondaryContainer = OnSecondaryContainerLumen
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
