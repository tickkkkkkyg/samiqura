/*
 * Omni Browser - A premium, private, and secure web browser.
 * Copyright (C) 2026 RebelRoot Ltd
 */

package com.samiqura.browser.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// ── Google-Quality Design Tokens ──

// Light mode neutrals
val LightBg = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVar = Color(0xFFF1F3F4)
val LightTextPrimary = Color(0xFF202124)
val LightTextSec = Color(0xFF5F6368)
val LightBorder = Color(0xFFDADCE0)
val LightError = Color(0xFFB3261E)

// Dark mode neutrals
val DarkBg = Color(0xFF131314)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVar = Color(0xFF28292A)
val DarkTextPrimary = Color(0xFFE3E3E3)
val DarkTextSec = Color(0xFF9AA0A6)
val DarkBorder = Color(0xFF444746)
val DarkError = Color(0xFFF2B8B5)

// AMOLED mode neutrals — true black for OLED power savings
val AmoledBg = Color(0xFF000000)
val AmoledSurface = Color(0xFF0A0A0A)
val AmoledSurfaceVar = Color(0xFF141414)
val AmoledBorder = Color(0xFF2A2A2A)

// ── Accent Theme Definitions ──

data class AccentPalette(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val error: Color
)

// Premium tailored accents based on Google Material standards
val OceanBlueDark = AccentPalette(Color(0xFF7BAFFF), Color(0xFF4C8DFF), Color(0xFF2A6BDA), DarkError)
val OceanBlueLight = AccentPalette(Color(0xFF1A73E8), Color(0xFF0D5AC4), Color(0xFF084294), LightError)

val CrimsonRedDark = AccentPalette(Color(0xFFFF6B6B), Color(0xFFFF5252), Color(0xFFE53935), DarkError)
val CrimsonRedLight = AccentPalette(Color(0xFFD32F2F), Color(0xFFB71C1C), Color(0xFF7F1010), LightError)

val EmeraldGreenDark = AccentPalette(Color(0xFF4CAF93), Color(0xFF26A69A), Color(0xFF00897B), DarkError)
val EmeraldGreenLight = AccentPalette(Color(0xFF00897B), Color(0xFF00695C), Color(0xFF004D40), LightError)

val SunsetOrangeDark = AccentPalette(Color(0xFFFF7A45), Color(0xFFFF8A5B), Color(0xFFFB542B), DarkError)
val SunsetOrangeLight = AccentPalette(Color(0xFFFB542B), Color(0xFFE64A19), Color(0xFFBF360C), LightError)

val RoyalPurpleDark = AccentPalette(Color(0xFFB388FF), Color(0xFF9C6AFF), Color(0xFF7C4DFF), DarkError)
val RoyalPurpleLight = AccentPalette(Color(0xFF7C4DFF), Color(0xFF651FFF), Color(0xFF4527A0), LightError)

val MonochromeDark = AccentPalette(Color(0xFFC7C7C7), Color(0xFFAAAAAA), Color(0xFF303030), DarkError)
val MonochromeLight = AccentPalette(Color(0xFF5E5E5E), Color(0xFF474747), Color(0xFF1F1F1F), LightError)

// Arc carries a full ground palette rather than just a primary recolour, so it
// needs a real entry here to stay selectable, plus a special case in
// getColorScheme that routes it to arcDarkScheme/arcLightScheme.
val ArcAccentPalette = AccentPalette(ArcAccent, ArcAccent, ArcAccentQuiet, DarkError)
val ArcAccentPaletteLight = AccentPalette(ArcAccentQuiet, ArcAccentQuiet, ArcAccent, LightError)

val AccentThemesDark = mapOf(
    "Arc" to ArcAccentPalette,
    "Ocean Blue" to OceanBlueDark,
    "Crimson Red" to CrimsonRedDark,
    "Emerald Green" to EmeraldGreenDark,
    "Sunset Orange" to SunsetOrangeDark,
    "Royal Purple" to RoyalPurpleDark,
    "Monochrome" to MonochromeDark
)

val AccentThemesLight = mapOf(
    "Arc" to ArcAccentPaletteLight,
    "Ocean Blue" to OceanBlueLight,
    "Crimson Red" to CrimsonRedLight,
    "Emerald Green" to EmeraldGreenLight,
    "Sunset Orange" to SunsetOrangeLight,
    "Royal Purple" to RoyalPurpleLight,
    "Monochrome" to MonochromeLight
)

// Default accent on first launch. The key stays "Arc" so that preferences
// saved by earlier builds keep resolving to the same scheme; the colours it
// maps to are the Brave-flavoured tokens in ArcTokens.kt.
const val DEFAULT_ACCENT = "Arc"

// Accent choices shown in the theme pickers, in display order. The default
// leads. Keys must exist in [AccentThemesDark]/[AccentThemesLight], or be
// [DEFAULT_ACCENT] (which routes to the token-built scheme instead).
val AccentSwatches = listOf(
    DEFAULT_ACCENT to ArcAccent,          // Brave orange (default)
    "Ocean Blue" to Color(0xFF1A73E8),
    "Crimson Red" to Color(0xFFD32F2F),
    "Emerald Green" to Color(0xFF00897B),
    "Sunset Orange" to Color(0xFFE64A19),
    "Royal Purple" to Color(0xFF7C4DFF),
    "Monochrome" to Color(0xFF5E5E5E)
)

fun buildDarkScheme(accent: AccentPalette): ColorScheme = darkColorScheme(
    background = DarkBg,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVar,
    primary = accent.primary,
    secondary = accent.secondary,
    tertiary = accent.tertiary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSec,
    outline = DarkBorder,
    outlineVariant = DarkBorder.copy(alpha = 0.5f),
    error = accent.error
)

fun buildLightScheme(accent: AccentPalette, isCreamy: Boolean = false): ColorScheme = lightColorScheme(
    background = if (isCreamy) Color(0xFFFAF8F5) else LightBg,
    surface = if (isCreamy) Color(0xFFFAF8F5) else LightSurface,
    surfaceVariant = if (isCreamy) Color(0xFFF3EFE9) else LightSurfaceVar,
    primary = accent.primary,
    secondary = accent.secondary,
    tertiary = accent.tertiary,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    onSurfaceVariant = LightTextSec,
    outline = LightBorder,
    outlineVariant = LightBorder.copy(alpha = 0.5f),
    error = accent.error
)

/**
 * Builds an AMOLED (true-black) dark scheme — same accent as chosen dark theme
 * but with pure-black background and near-black surfaces for OLED power savings.
 */
fun buildAmoledScheme(accent: AccentPalette): ColorScheme = darkColorScheme(
    background = AmoledBg,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurfaceVar,
    primary = accent.primary,
    secondary = accent.secondary,
    tertiary = accent.tertiary,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSec,
    outline = AmoledBorder,
    outlineVariant = AmoledBorder.copy(alpha = 0.5f),
    error = accent.error
)

// Legacy schemes for backward compat (default Ocean Blue)
val OmniDarkScheme = buildDarkScheme(OceanBlueDark)
val OmniLightScheme = buildLightScheme(OceanBlueLight)

/**
 * Returns the appropriate ColorScheme for the given theme mode, accent name, and context.
 *
 * @param accentTheme Accent name from [AccentThemesDark]/[AccentThemesLight]
 * @param isDark true for dark/AMOLED, false for light
 * @param isAmoled true to use pure-black AMOLED surfaces (only applies when isDark=true)
 * @param isDynamic true to extract palette from wallpaper (Android 12+ only)
 * @param context Android Context needed for dynamic color extraction
 */
fun getColorScheme(
    accentTheme: String,
    isDark: Boolean,
    isAmoled: Boolean = false,
    isCreamy: Boolean = false,
    isDynamic: Boolean = false,
    context: android.content.Context? = null
): ColorScheme {
    // Material You dynamic color (Android 12+)
    if (isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && context != null) {
        val dynamicScheme = if (isDark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
        // Apply AMOLED override on top of dynamic scheme if requested
        return if (isDark && isAmoled) {
            dynamicScheme.copy(
                background = AmoledBg,
                surface = AmoledSurface,
                surfaceVariant = AmoledSurfaceVar,
                outline = AmoledBorder,
                outlineVariant = AmoledBorder.copy(alpha = 0.5f)
            )
        } else {
            dynamicScheme
        }
    }

    // Arc replaces the whole ground, not just the primary, so it bypasses the
    // AccentPalette path entirely and uses the token-built schemes.
    if (accentTheme == DEFAULT_ACCENT) {
        return when {
            isDark && isAmoled -> arcDarkScheme().copy(
                background = AmoledBg,
                surface = AmoledSurface,
                surfaceVariant = AmoledSurfaceVar,
                outline = AmoledBorder,
                outlineVariant = AmoledBorder.copy(alpha = 0.5f)
            )
            isDark -> arcDarkScheme()
            else -> arcLightScheme()
        }
    }

    // Static accent palette
    val paletteMap = if (isDark) AccentThemesDark else AccentThemesLight
    val palette = paletteMap[accentTheme] ?: paletteMap["Ocean Blue"]!!
    return when {
        isDark && isAmoled -> buildAmoledScheme(palette)
        isDark             -> buildDarkScheme(palette)
        else               -> buildLightScheme(palette, isCreamy)
    }
}

// ── Single-source surface color helper ──

/**
 * Returns the correct sheet/dialog background color for the current theme.
 * Use this in ALL ModalBottomSheets, Dialogs, Cards, and elevated Surfaces
 * instead of hardcoding hex values.
 *
 * - AMOLED dark → Color.Black
 * - Regular dark → MaterialTheme surface (#1E1E1E)
 * - Light → MaterialTheme surface (White)
 */
@Composable
fun omniSheetContainerColor(): Color = MaterialTheme.colorScheme.surface

/**
 * Returns the drag-handle color for bottom sheets.
 * Always use this instead of hardcoded hex values.
 */
@Composable
fun omniDragHandleColor(): Color = MaterialTheme.colorScheme.outlineVariant
