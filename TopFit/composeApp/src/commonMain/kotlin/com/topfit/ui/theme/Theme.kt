package com.topfit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Soft,
    onPrimaryContainer = PrimaryDeep,
    secondary = Accent,
    onSecondary = Color.White,
    error = Danger,
    background = BgLight,
    onBackground = InkLight,
    surface = SurfaceLight,
    onSurface = InkLight,
    surfaceVariant = BgSoftLight,
    onSurfaceVariant = Ink2Light,
    outline = HairlineLight,
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryDeep,
    onPrimaryContainer = Soft,
    secondary = Accent,
    onSecondary = Color.White,
    error = Danger,
    background = BgDark,
    onBackground = InkDark,
    surface = SurfaceDark,
    onSurface = InkDark,
    surfaceVariant = BgSoftDark,
    onSurfaceVariant = Ink2Dark,
    outline = HairlineDark,
)

// Extra tokens not in Material3
data class TfColors(
    val ink3: Color,
    val soft: Color,
    val softDeep: Color,
    val hairline: Color,
    val bgSoft: Color,
    val accent: Color,
    val danger: Color,
)

val LocalTfColors = staticCompositionLocalOf {
    TfColors(Ink3Light, Soft, SoftDeep, HairlineLight, BgSoftLight, Accent, Danger)
}

@Composable
fun TopFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val tfColors = if (darkTheme)
        TfColors(Ink3Dark, SoftDeep, Soft, HairlineDark, BgSoftDark, Accent, Danger)
    else
        TfColors(Ink3Light, Soft, SoftDeep, HairlineLight, BgSoftLight, Accent, Danger)

    CompositionLocalProvider(LocalTfColors provides tfColors) {
        MaterialTheme(
            colorScheme = colors,
            typography = TopFitTypography,
            content = content,
        )
    }
}
