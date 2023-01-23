package dev.timatifey.posanie.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.timatifey.posanie.model.domain.AppColorScheme
import dev.timatifey.posanie.model.domain.AppTheme

private val LightPurpleColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    primaryContainer = Purple90,
    secondaryContainer = PurpleGrey90,
    surfaceVariant = DesaturatedPurpleGrey95
)

private val DarkPurpleColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    primaryContainer = Purple30,
    secondaryContainer = PurpleGrey30,
    surfaceVariant = DesaturatedPurpleGrey35
)

private val LightPinkColorScheme = lightColorScheme(
    primary = SaturatedPink40,
    secondary = PinkGrey40,
    tertiary = Sand40,
    primaryContainer = SaturatedPink90,
    secondaryContainer = PinkGrey90,
    surfaceVariant = DesaturatedPinkGrey95
)

private val DarkPinkColorScheme = darkColorScheme(
    primary = SaturatedPink80,
    secondary = PinkGrey80,
    tertiary = Sand80,
    primaryContainer = SaturatedPink30,
    secondaryContainer = PinkGrey30,
    surfaceVariant = DesaturatedPinkGrey35
)

private val LightGreenColorScheme = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Blue40,
    primaryContainer = Green90,
    secondaryContainer = GreenGrey90,
    surfaceVariant = DesaturatedGreenGrey95
)

private val DarkGreenColorScheme = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = Blue80,
    primaryContainer = Green30,
    secondaryContainer = GreenGrey30,
    surfaceVariant = DesaturatedGreenGrey35
)

private val ContrastColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    primaryContainer = Purple30,
    secondaryContainer = PurpleGrey30,
    surface = Black,
    surfaceVariant = DesaturatedPurpleGrey35,
    background = Black
)

@Composable
fun PoSanieTheme(
    appTheme: AppTheme,
    appColorScheme: AppColorScheme,
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val darkTheme = when(appTheme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    val canUseDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val purpleColorScheme = if (darkTheme) DarkPurpleColorScheme else LightPurpleColorScheme
    val pinkColorScheme = if (darkTheme) DarkPinkColorScheme else LightPinkColorScheme
    val greenColorScheme = if (darkTheme) DarkGreenColorScheme else LightGreenColorScheme
    val contrastColorScheme = if (darkTheme) ContrastColorScheme else LightGreenColorScheme

    val context = LocalContext.current
    val systemColorScheme = if (canUseDynamicColor) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        purpleColorScheme
    }

    val colorScheme = when(appColorScheme) {
        AppColorScheme.SYSTEM -> systemColorScheme
        AppColorScheme.PURPLE -> purpleColorScheme
        AppColorScheme.PINK -> pinkColorScheme
        AppColorScheme.GREEN -> greenColorScheme
        AppColorScheme.CONTRAST -> contrastColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val currentWindow = (view.context as? Activity)?.window
            ?: throw Exception("Not in an activity - unable to get Window reference")
        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(currentWindow, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}