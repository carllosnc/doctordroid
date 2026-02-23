package com.carlosnc.doctordroid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue400,
    onPrimary = Blue950,
    primaryContainer = Blue900,
    onPrimaryContainer = Blue100,
    secondary = Blue300,
    onSecondary = Blue950,
    secondaryContainer = Blue800,
    onSecondaryContainer = Blue100,
    tertiary = Blue200,
    onTertiary = Blue900,
    tertiaryContainer = Blue700,
    onTertiaryContainer = Blue50,
    background = Blue900, // Lightened from Blue950
    onBackground = Blue50,
    surface = Blue900, // Lightened from Blue950
    onSurface = Blue50,
    surfaceVariant = Blue800, // Lightened from Blue900 to maintain contrast
    onSurfaceVariant = Blue200,
    outline = Blue500,
    outlineVariant = Blue700
)

private val LightColorScheme = lightColorScheme(
    primary = Blue600,
    onPrimary = Blue50,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,
    secondary = Blue500,
    onSecondary = Blue50,
    secondaryContainer = Blue100,
    onSecondaryContainer = Blue800,
    tertiary = Blue400,
    onTertiary = Blue50,
    tertiaryContainer = Blue50,
    onTertiaryContainer = Blue950,
    background = Blue50,
    onBackground = Blue950,
    surface = Blue50,
    onSurface = Blue950,
    surfaceVariant = Blue100,
    onSurfaceVariant = Blue950,
    outline = Blue400,
    outlineVariant = Blue300
)

@Composable
fun DoctordroidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Set to false to see the custom dark theme colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
