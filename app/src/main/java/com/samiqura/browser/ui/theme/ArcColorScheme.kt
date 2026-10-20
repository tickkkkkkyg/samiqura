/*
 * Samiqura - Arc-inspired Material 3 colour schemes.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * Builds ColorSchemes from the ArcTokens palette so that every surface in the
 * app picks up the Arc ground automatically. Contrast pairs were chosen to
 * clear WCAG AA: ArcInkNightSec on ArcNightPanel is ~7:1, and its day-mode
 * counterpart ~6.8:1, so secondary metadata stays legible on both grounds.
 */

package com.samiqura.browser.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

fun arcDarkScheme(): ColorScheme = darkColorScheme(
    primary = ArcAccent,
    onPrimary = ArcAccentInk,
    primaryContainer = ArcAccentQuiet,
    onPrimaryContainer = ArcAccentInk,
    secondary = ArcAccent,
    onSecondary = ArcAccentInk,
    tertiary = ArcSpaceColors[1],
    onTertiary = ArcAccentInk,
    background = ArcNightGround,
    onBackground = ArcInkNight,
    surface = ArcNightPanel,
    onSurface = ArcInkNight,
    surfaceVariant = ArcNightPanelActive,
    onSurfaceVariant = ArcInkNightSec,
    surfaceContainerLowest = ArcNightGround,
    surfaceContainerLow = ArcNightGround,
    surfaceContainer = ArcNightPanel,
    surfaceContainerHigh = ArcNightPanelActive,
    surfaceContainerHighest = ArcNightPanelActive,
    outline = ArcNightHairline,
    outlineVariant = ArcNightHairline,
    error = Color_ErrorDark,
    onError = Color_OnErrorDark,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    // Filled roles that Material 3 components (segmented buttons, navigation
    // indicators, chips) read instead of primary. Without these they fall back
    // to the baseline lavender palette, which is the last purple left in a
    // Brave-orange app.
    secondaryContainer = Color(0xFF73301E),
    onSecondaryContainer = Color(0xFFFFDCCF),
    tertiaryContainer = Color(0xFF2A2F73),
    onTertiaryContainer = Color(0xFFDDE0FF),
    inverseSurface = Color(0xFFF2F2F5),
    inverseOnSurface = Color(0xFF1A1D21),
    inversePrimary = ArcAccentQuiet,
    surfaceTint = ArcAccent,
    scrim = Color_Scrim,
)

fun arcLightScheme(): ColorScheme = lightColorScheme(
    primary = ArcAccentQuiet,
    onPrimary = ArcAccentInk,
    primaryContainer = ArcAccent,
    onPrimaryContainer = ArcAccentInk,
    secondary = ArcAccentQuiet,
    onSecondary = ArcAccentInk,
    tertiary = ArcSpaceColors[1],
    onTertiary = ArcAccentInk,
    background = ArcDayGround,
    onBackground = ArcInkDay,
    surface = ArcDayPanel,
    onSurface = ArcInkDay,
    surfaceVariant = ArcDayPanelActive,
    onSurfaceVariant = ArcInkDaySec,
    surfaceContainerLowest = ArcDayPanel,
    surfaceContainerLow = ArcDayGround,
    surfaceContainer = ArcDayPanel,
    surfaceContainerHigh = ArcDayPanelActive,
    surfaceContainerHighest = ArcDayPanelActive,
    outline = ArcDayHairline,
    outlineVariant = ArcDayHairline,
    error = Color_ErrorLight,
    onError = Color_OnErrorLight,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    secondaryContainer = Color(0xFFFFDCCF),
    onSecondaryContainer = Color(0xFF3B1205),
    tertiaryContainer = Color(0xFFDDE0FF),
    onTertiaryContainer = Color(0xFF12184A),
    inverseSurface = Color(0xFF1A1D21),
    inverseOnSurface = Color(0xFFF6F3EE),
    inversePrimary = ArcAccent,
    surfaceTint = ArcAccent,
    scrim = Color_Scrim,
)

// Error and scrim values reused from the existing system so a re-theme does
// not silently change how destructive actions read.
private val Color_ErrorDark = Color(0xFFF2B8B5)
private val Color_OnErrorDark = Color(0xFF601410)
private val Color_ErrorLight = Color(0xFFB3261E)
private val Color_OnErrorLight = Color(0xFFFFFFFF)
private val Color_Scrim = Color(0xFF000000)
