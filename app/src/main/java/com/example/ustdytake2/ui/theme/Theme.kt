package com.example.ustdytake2.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Ink900,
    onPrimary = SurfaceWhite,
    secondary = Sky500,
    tertiary = Gold400,
    background = Cream50,
    onBackground = Ink900,
    surface = SurfaceWhite,
    onSurface = Ink900,
    surfaceVariant = Cream100,
    onSurfaceVariant = Ink700,
    error = Coral500,
    onError = SurfaceWhite
)

@Composable
fun UstdyTake2Theme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
