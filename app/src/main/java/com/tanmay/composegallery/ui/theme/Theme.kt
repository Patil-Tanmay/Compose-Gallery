package com.tanmay.composegallery.ui.theme

import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Composable
fun ComposeGalleryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val activity = LocalContext.current as ComponentActivity

    SideEffect {
        activity.window.statusBarColor = colors.primary.toArgb()
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content
    )
}


val LightColorPalette = lightColors(
    primary = LightPrimary,
    secondary = LightItemBackgroundL1,
    background = LightBackgroundL2,
    surface = LightBackgroundL1,
    error = LightError,
    onPrimary = Color.White,
    onSecondary = LightOn,
    onBackground = LightOn,
    onSurface = LightOn,
    onError = Color.White
)

val NightColorPalette = darkColors(
    primary = NightPrimary,
    secondary = NightItemBackgroundL1,
    background = NightBackgroundL2,
    surface = NightBackgroundL1,
    error = Error,
    onPrimary = NightOn,
    onSecondary = NightOn,
    onBackground = NightOn,
    onSurface = NightOn,
    onError = Color.White
)
