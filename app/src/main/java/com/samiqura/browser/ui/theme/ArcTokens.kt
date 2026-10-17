/*
 * Samiqura - Arc-inspired design tokens.
 *
 * Copyright (C) 2026 Samiqura contributors
 * Derived from Omni Browser (C) 2026 RebelRoot Ltd, GPL-3.0.
 *
 * This file is the single source of truth for the Arc-inspired visual system.
 * Components consume these tokens and must not hard-code hex values, so the
 * whole app re-themes from one place.
 *
 * Design intent: tabs are treated as *places* rather than a scrolling strip.
 * Colour is therefore quiet — a near-black ground with one saturated accent —
 * so the one thing that signals state (which Space you are in) stays legible.
 */

package com.samiqura.browser.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ── Dark ground ──────────────────────────────────────────────────────────────
// Deeper than a standard Material dark surface so the frosted panels read as
// genuinely translucent rather than merely grey.

val ArcNightGround = Color(0xFF0B0B0F)
val ArcNightPanel = Color(0xFF16161C)
val ArcNightPanelActive = Color(0xFF22222B)
val ArcNightHairline = Color(0xFF2E2E3A)

// ── Day ground ───────────────────────────────────────────────────────────────

val ArcDayGround = Color(0xFFF4F4F6)
val ArcDayPanel = Color(0xFFFFFFFF)
val ArcDayPanelActive = Color(0xFFE9E9EE)
val ArcDayHairline = Color(0xFFDCDCE3)

// ── Ink ──────────────────────────────────────────────────────────────────────

val ArcInkNight = Color(0xFFF2F2F5)
val ArcInkNightSec = Color(0xFF9C9CAB)
val ArcInkDay = Color(0xFF14141A)
val ArcInkDaySec = Color(0xFF5C5C6B)

// ── Accent ───────────────────────────────────────────────────────────────────
// The single saturated colour in the system. Used for the active Space, focus
// rings and primary action only — never as decoration. Chosen to sit clearly
// apart from the neutral ground without the "acid green" look that generic
// dark themes default to.

val ArcAccent = Color(0xFF7C6BF5)
val ArcAccentQuiet = Color(0xFF4B3FB0)
val ArcAccentInk = Color(0xFFFFFFFF)

// ── Space colours ────────────────────────────────────────────────────────────
// Every Space gets a distinguishable dot. These are read as small marks beside
// text, so they are tuned for contrast against both grounds rather than for
// maximum saturation.

val ArcSpaceColors = listOf(
    Color(0xFF7C6BF5), // violet
    Color(0xFF38BDF8), // sky
    Color(0xFF34D399), // emerald
    Color(0xFFFBBF24), // amber
    Color(0xFFFB7185), // rose
    Color(0xFFA78BFA), // lilac
    Color(0xFF2DD4BF), // teal
    Color(0xFFF97316), // ember
)

// ── Shape ────────────────────────────────────────────────────────────────────
// Arc reads as soft and pill-like. These are the only radii in the system.

object ArcRadius {
    val chip = 12.dp
    val panel = 22.dp
    val card = 28.dp
    val pill = 999.dp
    val sheet = 32.dp
    val dot = 6.dp
}

// ── Motion ───────────────────────────────────────────────────────────────────
// One duration scale. A browser that animates inconsistently feels cheap.

object ArcMotion {
    const val FAST_MILLIS = 140
    const val BASE_MILLIS = 220
    const val SLOW_MILLIS = 320
}
