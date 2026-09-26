package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val SilkRoadColorScheme = darkColorScheme(
    primary = SaffronGold,
    onPrimary = Color(0xFF1B1108),
    primaryContainer = Color(0xFF4B301B),
    onPrimaryContainer = BrightAmber,
    secondary = PersianTurquoise,
    onSecondary = Color(0xFF061E1B),
    secondaryContainer = Color(0xFF124640),
    onSecondaryContainer = Color(0xFFA8F0E6),
    tertiary = CrimsonSilk,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF581E12),
    onTertiaryContainer = Color(0xFFFFDAD2),
    background = DesertNightBg,
    onBackground = ParchmentLight,
    surface = CaravanseraiSurface,
    onSurface = ParchmentLight,
    surfaceVariant = SandstoneCard,
    onSurfaceVariant = ParchmentMuted,
    outline = SandstoneBorder,
    error = RubyBrocade,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = SilkRoadColorScheme,
            typography = Typography,
            content = content
        )
    }
}
