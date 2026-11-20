package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.ThemeMode

@Immutable
data class TravelSemanticColors(
    val budgetPositive: Color,
    val budgetPositiveContainer: Color,
    val budgetWarning: Color,
    val budgetNegative: Color,
    val budgetNegativeContainer: Color,
    val foodAccent: Color,
    val transportAccent: Color,
    val hotelAccent: Color,
    val otherAccent: Color
)

private val LightSemanticColors = TravelSemanticColors(
    budgetPositive = BudgetPositiveLight,
    budgetPositiveContainer = BudgetPositiveContainerLight,
    budgetWarning = BudgetWarningLight,
    budgetNegative = BudgetNegativeLight,
    budgetNegativeContainer = BudgetNegativeContainerLight,
    foodAccent = FoodAccentLight,
    transportAccent = TransportAccentLight,
    hotelAccent = HotelAccentLight,
    otherAccent = OtherAccentLight
)

private val DarkSemanticColors = TravelSemanticColors(
    budgetPositive = BudgetPositiveDark,
    budgetPositiveContainer = BudgetPositiveContainerDark,
    budgetWarning = BudgetWarningDark,
    budgetNegative = BudgetNegativeDark,
    budgetNegativeContainer = BudgetNegativeContainerDark,
    foodAccent = FoodAccentDark,
    transportAccent = TransportAccentDark,
    hotelAccent = HotelAccentDark,
    otherAccent = OtherAccentDark
)

val LocalTravelColors = staticCompositionLocalOf { LightSemanticColors }

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealPrimaryContainerDark,
    onPrimaryContainer = TealOnPrimaryContainerDark,
    secondary = TerracottaSecondaryDark,
    onSecondary = TerracottaOnSecondaryDark,
    secondaryContainer = TerracottaSecondaryContainerDark,
    onSecondaryContainer = TerracottaOnSecondaryContainerDark,
    tertiary = CompassGoldTertiaryDark,
    onTertiary = CompassGoldOnTertiaryDark,
    tertiaryContainer = CompassGoldContainerDark,
    onTertiaryContainer = CompassGoldOnTertiaryContainerDark,
    background = SlateBackgroundDark,
    onBackground = IvoryOnBackgroundDark,
    surface = SlateSurfaceDark,
    onSurface = IvoryOnBackgroundDark,
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFBFC8C6),
    surfaceContainerLow = SlateSurfaceContainerLowDark,
    surfaceContainer = SlateSurfaceContainerDark,
    surfaceContainerHigh = SlateSurfaceContainerHighDark,
    outline = OutlineSlateDark,
    outlineVariant = OutlineVariantSlateDark,
    error = BudgetNegativeDark,
    errorContainer = BudgetNegativeContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = TealOnPrimaryLight,
    primaryContainer = TealPrimaryContainerLight,
    onPrimaryContainer = TealOnPrimaryContainerLight,
    secondary = TerracottaSecondaryLight,
    onSecondary = TerracottaOnSecondaryLight,
    secondaryContainer = TerracottaSecondaryContainerLight,
    onSecondaryContainer = TerracottaOnSecondaryContainerLight,
    tertiary = CompassGoldTertiaryLight,
    onTertiary = CompassGoldOnTertiaryLight,
    tertiaryContainer = CompassGoldContainerLight,
    onTertiaryContainer = CompassGoldOnTertiaryContainerLight,
    background = SandBackgroundLight,
    onBackground = InkOnBackgroundLight,
    surface = WarmSurfaceLight,
    onSurface = InkOnBackgroundLight,
    surfaceVariant = WarmSurfaceVariantLight,
    onSurfaceVariant = Color(0xFF424847),
    surfaceContainerLow = WarmSurfaceContainerLowLight,
    surfaceContainer = WarmSurfaceContainerLight,
    surfaceContainerHigh = WarmSurfaceContainerHighLight,
    outline = OutlineWarmLight,
    outlineVariant = OutlineVariantWarmLight,
    error = BudgetNegativeLight,
    errorContainer = BudgetNegativeContainerLight
)

@Composable
fun TravelExpenseTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val semanticColors = if (isDark) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(LocalTravelColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
