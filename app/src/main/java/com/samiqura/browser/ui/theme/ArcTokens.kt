/*
 * Samiqura - design tokens for the Brave-flavoured visual system.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * This file is the single source of truth for the app's visual system.
 * Components consume these tokens and must not hard-code hex values, so the
 * whole app re-themes from one place.
 *
 * Design intent: a warm, card-based look in the spirit of Brave — an off-white
 * or warm near-black ground, white cards with hairline borders, and one
 * saturated orange accent that marks state and primary actions. The "Arc"
 * names are historical; the values are what matter.
 */

package com.samiqura.browser.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Dark ground ──────────────────────────────────────────────────────────────
// Brave's dark mode is a warm near-black, not purple. Panels float on it
// with a subtle hairline so cards read as separate surfaces rather than one
// flat sheet.

val ArcNightGround = Color(0xFF1A1D21)
val ArcNightPanel = Color(0xFF24282E)
val ArcNightPanelActive = Color(0xFF2E3338)
val ArcNightHairline = Color(0xFF3A4047)

// ── Day ground ───────────────────────────────────────────────────────────────
// Brave light is warm — an off-white / cream ground that makes white cards
// pop without harsh contrast. The hairline is a warm grey, not blue.

val ArcDayGround = Color(0xFFF6F3EE)
val ArcDayPanel = Color(0xFFFFFFFF)
val ArcDayPanelActive = Color(0xFFEFECE7)
val ArcDayHairline = Color(0xFFE6E0D6)

// ── Ink ──────────────────────────────────────────────────────────────────────

val ArcInkNight = Color(0xFFF2F2F5)
val ArcInkNightSec = Color(0xFF9C9CAB)
val ArcInkDay = Color(0xFF14141A)
val ArcInkDaySec = Color(0xFF5C5C6B)

// ── Accent ───────────────────────────────────────────────────────────────────
// Brave lion orange — the whole browser is themed around this single
// saturated accent so active state, focus rings and primary actions read
// instantly. Light scheme uses the quieter burnt-orange so it doesn't
// vibrate on white.

val ArcAccent = Color(0xFFFB542B)
val ArcAccentQuiet = Color(0xFFBE3C1A)
val ArcAccentInk = Color(0xFFFFFFFF)

// ── Space colours ────────────────────────────────────────────────────────────
// Every Space gets a distinguishable dot. These are read as small marks beside
// text, so they are tuned for contrast against both grounds rather than for
// maximum saturation.

val ArcSpaceColors = listOf(
    Color(0xFFFB542B), // brave orange
    Color(0xFF4C54D2), // brave blurple
    Color(0xFF2AC6A0), // brave teal
    Color(0xFFFBBF24), // amber
    Color(0xFFFF4B6E), // brave pink
    Color(0xFF9B6AF5), // violet (kept for variety)
    Color(0xFF20B6E0), // sky
    Color(0xFFFF8A2B), // warm ember
)

// ── Shape ────────────────────────────────────────────────────────────────────
// Brave is compact and card-y: modest radii, pill search bars, soft sheets.

object ArcRadius {
    val chip = 12.dp
    val panel = 16.dp
    val card = 20.dp
    val pill = 999.dp
    val sheet = 24.dp
    val dot = 6.dp
}

// ── Motion ───────────────────────────────────────────────────────────────────
// One duration scale. A browser that animates inconsistently feels cheap.

object ArcMotion {
    const val FAST_MILLIS = 140
    const val BASE_MILLIS = 220
    const val SLOW_MILLIS = 320
}
